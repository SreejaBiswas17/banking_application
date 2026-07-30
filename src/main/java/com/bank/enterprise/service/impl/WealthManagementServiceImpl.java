package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Constants;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.FixedDepositEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.FixedDepositRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.TransactionService;
import com.bank.enterprise.service.WealthManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WealthManagementServiceImpl implements WealthManagementService {

    private final FixedDepositRepository fdRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public FixedDepositEntity createFixedDeposit(Long customerId, Long accountId, BigDecimal amount, int tenureDays) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        if (account.getAvailableBalance().compareTo(amount) < 0) {
            throw new BankException("Insufficient funds in account to open Fixed Deposit");
        }

        // Deduct FD principal from liquid savings account
        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(account.getAccountNumber())
                .destinationAccountNumber("BANK_FD_POOL_ACC")
                .amount(amount)
                .description("Fixed Deposit Opening")
                .build());

        BigDecimal rate = Constants.DEFAULT_INTEREST_RATE_FIXED;
        double p = amount.doubleValue();
        double r = rate.doubleValue();
        double t = tenureDays / 365.0;
        double maturityVal = p * (1 + r * t);

        BigDecimal maturityAmount = BigDecimal.valueOf(maturityVal).setScale(2, RoundingMode.HALF_UP);
        String fdNum = "FD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        FixedDepositEntity fd = FixedDepositEntity.builder()
                .fdNumber(fdNum)
                .customer(customer)
                .linkedAccount(account)
                .principalAmount(amount)
                .interestRate(rate)
                .tenureDays(tenureDays)
                .maturityAmount(maturityAmount)
                .startDate(LocalDate.now())
                .maturityDate(LocalDate.now().plusDays(tenureDays))
                .isClosed(false)
                .build();

        FixedDepositEntity savedFd = fdRepository.save(fd);

        auditService.logAction("CREATE_FIXED_DEPOSIT", customer.getUser().getUsername(), "FIXED_DEPOSIT", savedFd.getFdId().toString(), null, "Created FD " + fdNum);

        return savedFd;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FixedDepositEntity> getCustomerFixedDeposits(Long customerId) {
        return fdRepository.findByCustomer_CustomerId(customerId);
    }

    @Override
    @Transactional
    public void processMaturedDeposits() {
        List<FixedDepositEntity> maturedList = fdRepository.findByIsClosedFalseAndMaturityDateLessThanEqual(LocalDate.now());
        for (FixedDepositEntity fd : maturedList) {
            transactionService.deposit(TransactionDto.DepositRequest.builder()
                    .accountNumber(fd.getLinkedAccount().getAccountNumber())
                    .amount(fd.getMaturityAmount())
                    .description("Maturity Payout for FD: " + fd.getFdNumber())
                    .build());

            fd.setIsClosed(true);
            fdRepository.save(fd);

            auditService.logAction("FD_MATURITY_PAYOUT", "SYSTEM", "FIXED_DEPOSIT", fd.getFdId().toString(), null, "Credited " + fd.getMaturityAmount() + " to account " + fd.getLinkedAccount().getAccountNumber());
        }
    }
}
