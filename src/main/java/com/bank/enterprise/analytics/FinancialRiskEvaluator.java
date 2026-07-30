package com.bank.enterprise.analytics;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FinancialRiskEvaluator {

    public String evaluateCustomerRiskCategory(int creditScore, BigDecimal totalDebt, BigDecimal monthlyIncome) {
        if (monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return "HIGH_RISK";
        }

        BigDecimal dtiRatio = totalDebt.divide(monthlyIncome, 4, RoundingMode.HALF_UP);

        if (creditScore >= 750 && dtiRatio.compareTo(new BigDecimal("0.35")) < 0) {
            return "LOW_RISK";
        } else if (creditScore >= 650 && dtiRatio.compareTo(new BigDecimal("0.50")) < 0) {
            return "MODERATE_RISK";
        } else {
            return "HIGH_RISK";
        }
    }
}
