package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.EmiStatus;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.LoanEligibilityException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.EmiScheduleEntity;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.EmiScheduleRepository;
import com.bank.enterprise.repository.LoanRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.LoanService;
import com.bank.enterprise.service.NotificationService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanService {

    private final LoanRepository loanRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final CustomerRepository customerRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public LoanDto.LoanResponse applyForLoan(LoanDto.LoanApplicationRequest request) {
        CustomerEntity customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new LoanEligibilityException("Customer KYC is not verified");
        }

        BigDecimal interestRate = getInterestRateForLoanType(request.getLoanType());
        BigDecimal emi = calculateEMI(request.getPrincipalAmount(), interestRate, request.getTenureMonths());

        String loanNumber = "LN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LoanEntity loan = LoanEntity.builder()
                .loanNumber(loanNumber)
                .customer(customer)
                .loanType(request.getLoanType())
                .principalAmount(request.getPrincipalAmount())
                .outstandingPrincipal(request.getPrincipalAmount())
                .annualInterestRate(interestRate)
                .tenureMonths(request.getTenureMonths())
                .monthlyEmi(emi)
                .status(LoanStatus.SUBMITTED)
                .build();

        LoanEntity savedLoan = loanRepository.save(loan);

        auditService.logAction("LOAN_APPLICATION_SUBMITTED", customer.getUser().getUsername(), "LOAN", savedLoan.getLoanId().toString(), null, "Applied for " + request.getLoanType() + " loan of " + request.getPrincipalAmount());

        return mapToLoanResponse(savedLoan, null);
    }

    @Override
    @Transactional
    public LoanDto.LoanResponse approveLoan(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.SUBMITTED && loan.getStatus() != LoanStatus.UNDER_REVIEW) {
            throw new BankException("Loan cannot be approved in current status: " + loan.getStatus());
        }

        loan.setStatus(LoanStatus.APPROVED);
        LoanEntity updated = loanRepository.save(loan);

        // Generate EMI Amortization Schedule
        List<EmiScheduleEntity> schedules = generateAmortizationSchedule(updated);
        emiScheduleRepository.saveAll(schedules);

        auditService.logAction("LOAN_APPROVED", "LOAN_OFFICER", "LOAN", loanId.toString(), null, "Loan Approved");
        notificationService.sendNotification(loan.getCustomer().getUser().getUserId(), "LOAN_APPROVED", "Your loan " + loan.getLoanNumber() + " has been approved!");

        return mapToLoanResponse(updated, schedules);
    }

    @Override
    @Transactional
    public LoanDto.LoanResponse disburseLoan(Long loanId, String destinationAccountNumber) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        if (loan.getStatus() != LoanStatus.APPROVED) {
            throw new BankException("Only approved loans can be disbursed");
        }

        loan.setStatus(LoanStatus.DISBURSED);
        loan.setDisbursementDate(LocalDate.now());
        LoanEntity updated = loanRepository.save(loan);

        // Deposit loan principal into customer account
        transactionService.deposit(TransactionDto.DepositRequest.builder()
                .accountNumber(destinationAccountNumber)
                .amount(loan.getPrincipalAmount())
                .description("Loan Disbursement: " + loan.getLoanNumber())
                .build());

        auditService.logAction("LOAN_DISBURSED", "SYSTEM", "LOAN", loanId.toString(), null, "Disbursed " + loan.getPrincipalAmount() + " to account " + destinationAccountNumber);

        List<EmiScheduleEntity> schedules = emiScheduleRepository.findByLoan_LoanIdOrderByInstallmentNumberAsc(loanId);
        return mapToLoanResponse(updated, schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public LoanDto.LoanResponse getLoanById(Long loanId) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));
        List<EmiScheduleEntity> schedules = emiScheduleRepository.findByLoan_LoanIdOrderByInstallmentNumberAsc(loanId);
        return mapToLoanResponse(loan, schedules);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanDto.LoanResponse> getLoansByCustomerId(Long customerId) {
        return loanRepository.findByCustomer_CustomerId(customerId).stream()
                .map(loan -> mapToLoanResponse(loan, emiScheduleRepository.findByLoan_LoanIdOrderByInstallmentNumberAsc(loan.getLoanId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void payLoanEmi(Long scheduleId, String sourceAccountNumber) {
        EmiScheduleEntity schedule = emiScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("EmiSchedule", "id", scheduleId));

        if (schedule.getStatus() == EmiStatus.PAID) {
            throw new BankException("EMI installment is already paid");
        }

        LoanEntity loan = schedule.getLoan();

        // Process funds transfer for EMI
        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(sourceAccountNumber)
                .destinationAccountNumber("BANK_LOAN_POOL_ACC")
                .amount(schedule.getTotalEmiAmount())
                .description("EMI Installment #" + schedule.getInstallmentNumber() + " for Loan " + loan.getLoanNumber())
                .build());

        schedule.setPaidAmount(schedule.getTotalEmiAmount());
        schedule.setStatus(EmiStatus.PAID);
        schedule.setPaymentDate(LocalDate.now());
        emiScheduleRepository.save(schedule);

        // Reduce outstanding principal
        loan.setOutstandingPrincipal(loan.getOutstandingPrincipal().subtract(schedule.getPrincipalComponent()));
        if (loan.getOutstandingPrincipal().compareTo(BigDecimal.ZERO) <= 0) {
            loan.setStatus(LoanStatus.CLOSED);
        }
        loanRepository.save(loan);

        auditService.logAction("EMI_PAYMENT", "SYSTEM", "LOAN", loan.getLoanId().toString(), null, "Paid EMI #" + schedule.getInstallmentNumber());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LoanDto.LoanResponse> getAllLoans(Pageable pageable) {
        return loanRepository.findAll(pageable)
                .map(loan -> mapToLoanResponse(loan, null));
    }

    private BigDecimal getInterestRateForLoanType(com.bank.enterprise.common.LoanType type) {
        switch (type) {
            case HOME: return new BigDecimal("0.085"); // 8.5%
            case AUTO: return new BigDecimal("0.095"); // 9.5%
            case PERSONAL: return new BigDecimal("0.120"); // 12%
            case EDUCATION: return new BigDecimal("0.075"); // 7.5%
            case BUSINESS: return new BigDecimal("0.135"); // 13.5%
            default: return new BigDecimal("0.100");
        }
    }

    private BigDecimal calculateEMI(BigDecimal principal, BigDecimal rate, int months) {
        double p = principal.doubleValue();
        double r = rate.doubleValue() / 12;
        double n = months;
        double emi = (p * r * Math.pow(1 + r, n)) / (Math.pow(1 + r, n) - 1);
        return BigDecimal.valueOf(emi).setScale(2, RoundingMode.HALF_UP);
    }

    private List<EmiScheduleEntity> generateAmortizationSchedule(LoanEntity loan) {
        List<EmiScheduleEntity> list = new ArrayList<>();
        BigDecimal balance = loan.getPrincipalAmount();
        BigDecimal monthlyRate = loan.getAnnualInterestRate().divide(new BigDecimal("12"), 6, RoundingMode.HALF_UP);

        LocalDate dueDate = LocalDate.now().plusMonths(1);

        for (int i = 1; i <= loan.getTenureMonths(); i++) {
            BigDecimal interest = balance.multiply(monthlyRate).setScale(2, RoundingMode.HALF_UP);
            BigDecimal principalComp = loan.getMonthlyEmi().subtract(interest);
            balance = balance.subtract(principalComp);

            EmiScheduleEntity schedule = EmiScheduleEntity.builder()
                    .loan(loan)
                    .installmentNumber(i)
                    .dueDate(dueDate)
                    .principalComponent(principalComp)
                    .interestComponent(interest)
                    .totalEmiAmount(loan.getMonthlyEmi())
                    .paidAmount(BigDecimal.ZERO)
                    .status(EmiStatus.UNPAID)
                    .build();

            list.add(schedule);
            dueDate = dueDate.plusMonths(1);
        }
        return list;
    }

    private LoanDto.LoanResponse mapToLoanResponse(LoanEntity entity, List<EmiScheduleEntity> schedules) {
        List<LoanDto.EmiScheduleDto> emiDtos = schedules != null ? schedules.stream()
                .map(s -> LoanDto.EmiScheduleDto.builder()
                        .scheduleId(s.getScheduleId())
                        .installmentNumber(s.getInstallmentNumber())
                        .dueDate(s.getDueDate())
                        .principalComponent(s.getPrincipalComponent())
                        .interestComponent(s.getInterestComponent())
                        .totalEmiAmount(s.getTotalEmiAmount())
                        .paidAmount(s.getPaidAmount())
                        .status(s.getStatus())
                        .paymentDate(s.getPaymentDate())
                        .build())
                .collect(Collectors.toList()) : null;

        String customerName = entity.getCustomer().getFirstName() + " " + entity.getCustomer().getLastName();

        return LoanDto.LoanResponse.builder()
                .loanId(entity.getLoanId())
                .loanNumber(entity.getLoanNumber())
                .customerId(entity.getCustomer().getCustomerId())
                .customerName(customerName)
                .loanType(entity.getLoanType())
                .principalAmount(entity.getPrincipalAmount())
                .outstandingPrincipal(entity.getOutstandingPrincipal())
                .annualInterestRate(entity.getAnnualInterestRate())
                .tenureMonths(entity.getTenureMonths())
                .monthlyEmi(entity.getMonthlyEmi())
                .status(entity.getStatus())
                .disbursementDate(entity.getDisbursementDate())
                .createdAt(entity.getCreatedAt())
                .emiSchedules(emiDtos)
                .build();
    }
}
