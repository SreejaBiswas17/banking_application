package com.bank.enterprise.analytics;

import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.LoanEntity;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class CreditScoreEngine {

    public int calculateFicoCreditScore(List<AccountEntity> accounts, List<LoanEntity> loans, int totalTransactions, int defaultsCount) {
        int baseScore = 650;

        // Balance Factor (+/- 100 points)
        BigDecimal totalBalance = accounts.stream()
                .map(AccountEntity::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalBalance.compareTo(new BigDecimal("50000.00")) > 0) {
            baseScore += 75;
        } else if (totalBalance.compareTo(new BigDecimal("10000.00")) > 0) {
            baseScore += 45;
        } else if (totalBalance.compareTo(new BigDecimal("1000.00")) < 0) {
            baseScore -= 30;
        }

        // Transaction Volume Factor
        if (totalTransactions > 100) {
            baseScore += 50;
        } else if (totalTransactions > 30) {
            baseScore += 25;
        }

        // Loan History Factor
        if (defaultsCount > 0) {
            baseScore -= (defaultsCount * 120);
        } else if (!loans.isEmpty()) {
            baseScore += 40;
        }

        return Math.max(300, Math.min(850, baseScore));
    }
}
