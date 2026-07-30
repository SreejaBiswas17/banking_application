package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CREDIT_CARD_BILLING_CYCLES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardBillingCycleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BILLING_ID")
    private Long billingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CARD_ID", nullable = false)
    private CardEntity card;

    @Column(name = "STATEMENT_DATE", nullable = false)
    private LocalDate statementDate;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "TOTAL_STATEMENT_BALANCE", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalStatementBalance;

    @Column(name = "MINIMUM_DUE_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal minimumDueAmount;

    @Column(name = "PAID_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(name = "IS_FULLY_PAID", nullable = false)
    @Builder.Default
    private Boolean isFullyPaid = false;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
