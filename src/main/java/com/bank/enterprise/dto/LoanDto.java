package com.bank.enterprise.dto;

import com.bank.enterprise.common.EmiStatus;
import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.common.LoanType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class LoanDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanApplicationRequest {
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotNull(message = "Loan type is required")
        private LoanType loanType;

        @NotNull(message = "Principal amount is required")
        @DecimalMin(value = "1000.00", message = "Minimum loan amount is 1000.00")
        private BigDecimal principalAmount;

        @NotNull(message = "Tenure in months is required")
        @Min(value = 6, message = "Minimum tenure is 6 months")
        private Integer tenureMonths;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoanResponse {
        private Long loanId;
        private String loanNumber;
        private Long customerId;
        private String customerName;
        private LoanType loanType;
        private BigDecimal principalAmount;
        private BigDecimal outstandingPrincipal;
        private BigDecimal annualInterestRate;
        private Integer tenureMonths;
        private BigDecimal monthlyEmi;
        private LoanStatus status;
        private LocalDate disbursementDate;
        private LocalDateTime createdAt;
        private List<EmiScheduleDto> emiSchedules;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmiScheduleDto {
        private Long scheduleId;
        private Integer installmentNumber;
        private LocalDate dueDate;
        private BigDecimal principalComponent;
        private BigDecimal interestComponent;
        private BigDecimal totalEmiAmount;
        private BigDecimal paidAmount;
        private EmiStatus status;
        private LocalDate paymentDate;
    }
}
