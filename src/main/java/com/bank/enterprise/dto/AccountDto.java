package com.bank.enterprise.dto;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AccountDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountCreateRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotNull(message = "Account type is required")
        private AccountType accountType;

        private Currency currency;

        @DecimalMin(value = "0.0", message = "Initial deposit cannot be negative")
        private BigDecimal initialDeposit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountResponse {
        private Long accountId;
        private String accountNumber;
        private Long customerId;
        private String customerFullName;
        private AccountType accountType;
        private Currency currency;
        private BigDecimal balance;
        private BigDecimal availableBalance;
        private AccountStatus accountStatus;
        private BigDecimal overdraftLimit;
        private BigDecimal interestRate;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BalanceResponse {
        private String accountNumber;
        private BigDecimal ledgerBalance;
        private BigDecimal availableBalance;
        private Currency currency;
    }
}
