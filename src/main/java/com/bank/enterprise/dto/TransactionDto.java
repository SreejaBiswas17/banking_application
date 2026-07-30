package com.bank.enterprise.dto;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest {
        @NotBlank(message = "Source account number is required")
        private String sourceAccountNumber;

        @NotBlank(message = "Destination account number is required")
        private String destinationAccountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum transfer amount is 1.00")
        private BigDecimal amount;

        private TransactionType transferType; // INTERNAL_TRANSFER, NEFT_TRANSFER, RTGS_TRANSFER, IMPS_TRANSFER

        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositRequest {
        @NotBlank(message = "Account number is required")
        private String accountNumber;

        @NotNull(message = "Amount is required")
        @DecimalMin(value = "1.00", message = "Minimum deposit amount is 1.00")
        private BigDecimal amount;

        private String description;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse {
        private Long transactionId;
        private String transactionReference;
        private String sourceAccountNumber;
        private String destinationAccountNumber;
        private TransactionType transactionType;
        private BigDecimal amount;
        private BigDecimal feeAmount;
        private Currency currency;
        private TransactionStatus status;
        private String description;
        private String failureReason;
        private LocalDateTime initiatedAt;
        private LocalDateTime completedAt;
    }
}
