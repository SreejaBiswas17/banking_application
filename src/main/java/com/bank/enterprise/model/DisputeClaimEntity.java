package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "DISPUTE_CLAIMS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DisputeClaimEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DISPUTE_ID")
    private Long disputeId;

    @Column(name = "CLAIM_NUMBER", nullable = false, unique = true, length = 30)
    private String claimNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID", nullable = false)
    private TransactionEntity transaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "DISPUTE_REASON", nullable = false, length = 255)
    private String disputeReason;

    @Column(name = "DISPUTED_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal disputedAmount;

    @Column(name = "STATUS", nullable = false, length = 20) // SUBMITTED, UNDER_REVIEW, APPROVED_REFUND, REJECTED
    private String status;

    @Column(name = "RESOLUTION_REMARKS", length = 1000)
    private String resolutionRemarks;

    @CreationTimestamp
    @Column(name = "FILED_AT", nullable = false, updatable = false)
    private LocalDateTime filedAt;
}
