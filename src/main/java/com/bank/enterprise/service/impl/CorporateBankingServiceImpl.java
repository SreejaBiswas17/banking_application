package com.bank.enterprise.service.impl;

import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CorporateAccountRepository;
import com.bank.enterprise.repository.PayrollBatchRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.CorporateBankingService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CorporateBankingServiceImpl implements CorporateBankingService {

    private final CorporateAccountRepository corporateRepository;
    private final PayrollBatchRepository payrollRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public CorporateAccountEntity registerCorporateAccount(String companyName, String regNum, String taxId, Long primaryAccountId, BigDecimal creditLimit) {
        AccountEntity primary = accountRepository.findById(primaryAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", primaryAccountId));

        CorporateAccountEntity corp = CorporateAccountEntity.builder()
                .companyName(companyName)
                .registrationNumber(regNum)
                .taxId(taxId)
                .primaryAccount(primary)
                .creditLineLimit(creditLimit)
                .build();

        CorporateAccountEntity saved = corporateRepository.save(corp);
        auditService.logAction("REGISTER_CORPORATE_ACCOUNT", "SYSTEM", "CORPORATE_ACCOUNT", saved.getCorporateId().toString(), null, "Registered: " + companyName);
        return saved;
    }

    @Override
    @Transactional
    public PayrollBatchEntity executePayrollBatch(Long corporateId, int totalEmployees, BigDecimal totalAmount) {
        CorporateAccountEntity corp = corporateRepository.findById(corporateId)
                .orElseThrow(() -> new ResourceNotFoundException("CorporateAccount", "id", corporateId));

        AccountEntity account = corp.getPrimaryAccount();

        if (account.getAvailableBalance().compareTo(totalAmount) < 0) {
            throw new BankException("Insufficient balance in corporate account for bulk payroll execution");
        }

        // Deduct payroll total from corporate account
        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(account.getAccountNumber())
                .destinationAccountNumber("BANK_PAYROLL_DISBURSEMENT_POOL")
                .amount(totalAmount)
                .description("Bulk Salary Payroll Disbursement (" + totalEmployees + " employees)")
                .build());

        String ref = "PAYROLL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PayrollBatchEntity batch = PayrollBatchEntity.builder()
                .batchReference(ref)
                .corporateAccount(corp)
                .totalEmployees(totalEmployees)
                .totalPayrollAmount(totalAmount)
                .status("COMPLETED")
                .build();

        PayrollBatchEntity savedBatch = payrollRepository.save(batch);

        auditService.logAction("EXECUTE_PAYROLL_BATCH", "CORPORATE_ADMIN", "PAYROLL_BATCH", savedBatch.getBatchId().toString(), null, "Disbursed " + totalAmount + " for " + totalEmployees + " employees");
        return savedBatch;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PayrollBatchEntity> getCorporatePayrollHistory(Long corporateId) {
        return payrollRepository.findByCorporateAccount_CorporateId(corporateId);
    }
}
