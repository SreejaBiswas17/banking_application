package com.bank.enterprise.service;

import java.math.BigDecimal;

public interface AnalyticsService {
    int getCustomerCreditScore(Long customerId);
    String getCustomerRiskProfile(Long customerId, BigDecimal estimatedMonthlyIncome);
}
