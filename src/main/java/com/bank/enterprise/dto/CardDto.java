package com.bank.enterprise.dto;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.common.CardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CardDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardIssueRequest {
        @NotNull(message = "Account ID is required")
        private Long accountId;

        @NotNull(message = "Card type is required")
        private CardType cardType;

        @NotBlank(message = "4-digit PIN is required")
        @Pattern(regexp = "^\\d{4}$", message = "PIN must be exactly 4 numeric digits")
        private String pin;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardResponse {
        private Long cardId;
        private String cardNumberMasked;
        private String accountNumber;
        private CardType cardType;
        private LocalDate expiryDate;
        private CardStatus cardStatus;
        private BigDecimal dailyAtmLimit;
        private BigDecimal dailyPosLimit;
        private BigDecimal creditLimit;
        private BigDecimal usedCredit;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CardPinChangeRequest {
        @NotBlank(message = "Old PIN is required")
        private String oldPin;

        @NotBlank(message = "New PIN is required")
        @Pattern(regexp = "^\\d{4}$", message = "New PIN must be 4 digits")
        private String newPin;
    }
}
