package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "AML_SCREENINGS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmlScreeningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCREENING_ID")
    private Long screeningId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "RISK_SCORE", nullable = false, precision = 5, scale = 2)
    private BigDecimal riskScore; // 0.00 to 100.00

    @Column(name = "RISK_LEVEL", nullable = false, length = 20) // LOW, MEDIUM, HIGH, CRITICAL
    private String riskLevel;

    @Column(name = "PEP_STATUS", nullable = false) // Politically Exposed Person
    private Boolean isPep;

    @Column(name = "SANCTION_MATCH", nullable = false)
    private Boolean isSanctionMatch;

    @Column(name = "SCREENING_REMARKS", length = 1000)
    private String screeningRemarks;

    @CreationTimestamp
    @Column(name = "SCREENED_AT", nullable = false, updatable = false)
    private LocalDateTime screenedAt;
}
