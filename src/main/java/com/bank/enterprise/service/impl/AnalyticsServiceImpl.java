package com.bank.enterprise.service.impl;

import com.bank.enterprise.analytics.CreditScoreEngine;
import com.bank.enterprise.analytics.FinancialRiskEvaluator;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.LoanRepository;
import com.bank.enterprise.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final CreditScoreEngine creditScoreEngine;
    private final FinancialRiskEvaluator riskEvaluator;

    @Override
    @Transactional(readOnly = true)
    public int getCustomerCreditScore(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }

        List<AccountEntity> accounts = accountRepository.findByCustomer_CustomerId(customerId);
        List<LoanEntity> loans = loanRepository.findByCustomer_CustomerId(customerId);

        return creditScoreEngine.calculateFicoCreditScore(accounts, loans, 50, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public String getCustomerRiskProfile(Long customerId, BigDecimal estimatedMonthlyIncome) {
        int score = getCustomerCreditScore(customerId);
        List<LoanEntity> loans = loanRepository.findByCustomer_CustomerId(customerId);
        BigDecimal totalDebt = loans.stream()
                .map(LoanEntity::getOutstandingPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return riskEvaluator.evaluateCustomerRiskCategory(score, totalDebt, estimatedMonthlyIncome != null ? estimatedMonthlyIncome : new BigDecimal("5000.00"));
    }
}
