package com.bank.enterprise.service;

import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CorporateAccountRepository;
import com.bank.enterprise.repository.PayrollBatchRepository;
import com.bank.enterprise.service.impl.CorporateBankingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CorporateBankingServiceTest {

    @Mock
    private CorporateAccountRepository corporateRepository;

    @Mock
    private PayrollBatchRepository payrollRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CorporateBankingServiceImpl corporateService;

    private AccountEntity primaryAccount;
    private CorporateAccountEntity corporateAccount;

    @BeforeEach
    void setUp() {
        primaryAccount = AccountEntity.builder()
                .accountId(1L)
                .accountNumber("9900112233")
                .balance(new BigDecimal("100000.00"))
                .availableBalance(new BigDecimal("100000.00"))
                .build();

        corporateAccount = CorporateAccountEntity.builder()
                .corporateId(10L)
                .companyName("Acme Corp")
                .registrationNumber("REG-123")
                .taxId("TAX-123")
                .primaryAccount(primaryAccount)
                .creditLineLimit(new BigDecimal("500000.00"))
                .build();
    }

    @Test
    @DisplayName("Should successfully execute bulk payroll batch")
    void executePayrollBatch_Success() {
        when(corporateRepository.findById(10L)).thenReturn(Optional.of(corporateAccount));

        PayrollBatchEntity batch = PayrollBatchEntity.builder()
                .batchId(100L)
                .batchReference("PAYROLL-12345678")
                .corporateAccount(corporateAccount)
                .totalEmployees(50)
                .totalPayrollAmount(new BigDecimal("50000.00"))
                .status("COMPLETED")
                .build();

        when(payrollRepository.save(any(PayrollBatchEntity.class))).thenReturn(batch);

        PayrollBatchEntity result = corporateService.executePayrollBatch(10L, 50, new BigDecimal("50000.00"));

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("COMPLETED");
        verify(transactionService, times(1)).transferFunds(any());
    }
}
