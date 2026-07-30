package com.bank.enterprise.service;

import com.bank.enterprise.analytics.CreditScoreEngine;
import com.bank.enterprise.analytics.FinancialRiskEvaluator;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.LoanRepository;
import com.bank.enterprise.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private CreditScoreEngine creditScoreEngine;

    @Mock
    private FinancialRiskEvaluator riskEvaluator;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    @Test
    @DisplayName("Should return credit score for valid customer")
    void getCustomerCreditScore_Success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(accountRepository.findByCustomer_CustomerId(1L)).thenReturn(Collections.emptyList());
        when(loanRepository.findByCustomer_CustomerId(1L)).thenReturn(Collections.emptyList());
        when(creditScoreEngine.calculateFicoCreditScore(any(), any(), eq(50), eq(0))).thenReturn(740);

        int score = analyticsService.getCustomerCreditScore(1L);

        assertThat(score).isEqualTo(740);
    }
}
