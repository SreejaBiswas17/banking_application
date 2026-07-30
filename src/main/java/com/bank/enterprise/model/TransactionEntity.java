package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "TRANSACTIONS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSACTION_ID")
    private Long transactionId;

    @Column(name = "TRANSACTION_REFERENCE", nullable = false, unique = true, length = 36)
    private String transactionReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_ACCOUNT_ID")
    private AccountEntity sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESTINATION_ACCOUNT_ID")
    private AccountEntity destinationAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(name = "FEE_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal feeAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    @Builder.Default
    private Currency currency = Currency.USD;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private TransactionStatus status = TransactionStatus.PENDING_VERIFICATION;

    @Column(name = "DESCRIPTION", length = 255)
    private String description;

    @Column(name = "FAILURE_REASON", length = 255)
    private String failureReason;

    @CreationTimestamp
    @Column(name = "INITIATED_AT", nullable = false, updatable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "COMPLETED_AT")
    private LocalDateTime completedAt;
}
