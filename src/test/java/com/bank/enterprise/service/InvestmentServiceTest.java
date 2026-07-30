package com.bank.enterprise.service;

import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.MutualFundHoldingRepository;
import com.bank.enterprise.repository.MutualFundSchemeRepository;
import com.bank.enterprise.service.impl.InvestmentServiceImpl;
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
class InvestmentServiceTest {

    @Mock
    private MutualFundSchemeRepository schemeRepository;

    @Mock
    private MutualFundHoldingRepository holdingRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private InvestmentServiceImpl investmentService;

    private CustomerEntity customer;
    private AccountEntity account;
    private MutualFundSchemeEntity scheme;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder().customerId(1L).build();
        account = AccountEntity.builder().accountId(10L).accountNumber("100200300").availableBalance(new BigDecimal("10000.00")).build();
        scheme = MutualFundSchemeEntity.builder().schemeId(100L).schemeCode("EQUITY_GROWTH").schemeName("Enterprise Growth Fund").currentNav(new BigDecimal("50.00")).build();
    }

    @Test
    @DisplayName("Should successfully buy mutual fund units")
    void buyMutualFundUnits_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(accountRepository.findById(10L)).thenReturn(Optional.of(account));
        when(schemeRepository.findBySchemeCode("EQUITY_GROWTH")).thenReturn(Optional.of(scheme));
        when(holdingRepository.findByCustomer_CustomerIdAndScheme_SchemeId(1L, 100L)).thenReturn(Optional.empty());

        MutualFundHoldingEntity holding = MutualFundHoldingEntity.builder()
                .holdingId(500L)
                .customer(customer)
                .scheme(scheme)
                .totalUnits(new BigDecimal("100.0000"))
                .totalInvestedAmount(new BigDecimal("5000.00"))
                .build();

        when(holdingRepository.save(any(MutualFundHoldingEntity.class))).thenReturn(holding);

        MutualFundHoldingEntity result = investmentService.buyMutualFundUnits(1L, 10L, "EQUITY_GROWTH", new BigDecimal("5000.00"));

        assertThat(result).isNotNull();
        assertThat(result.getTotalInvestedAmount()).isEqualByComparingTo("5000.00");
        verify(transactionService, times(1)).transferFunds(any());
    }
}
