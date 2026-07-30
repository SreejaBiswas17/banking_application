package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FRAUD_LOGS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FraudLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FRAUD_LOG_ID")
    private Long fraudLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSACTION_ID")
    private TransactionEntity transaction;

    @Column(name = "SUSPICION_REASON", nullable = false, length = 255)
    private String suspicionReason;

    @Column(name = "FRAUD_SCORE", nullable = false, precision = 5, scale = 2)
    private BigDecimal fraudScore;

    @Column(name = "STATUS", nullable = false, length = 20) // UNDER_INVESTIGATION, CONFIRMED_FRAUD, FALSE_POSITIVE, CLEARED
    private String status;

    @Column(name = "INVESTIGATOR_NOTES", length = 1000)
    private String investigatorNotes;

    @CreationTimestamp
    @Column(name = "DETECTED_AT", nullable = false, updatable = false)
    private LocalDateTime detectedAt;
}
