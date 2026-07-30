package com.bank.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ReportDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialSummaryDto {
        private Long totalCustomers;
        private Long totalAccounts;
        private BigDecimal totalBankDeposits;
        private BigDecimal totalLoansDisbursed;
        private BigDecimal totalOutstandingLoans;
        private Long activeCardsCount;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountStatementDto {
        private String accountNumber;
        private String customerName;
        private LocalDate startDate;
        private LocalDate endDate;
        private BigDecimal openingBalance;
        private BigDecimal closingBalance;
        private BigDecimal totalDeposits;
        private BigDecimal totalWithdrawals;
        private List<TransactionDto.TransactionResponse> transactions;
    }
}
