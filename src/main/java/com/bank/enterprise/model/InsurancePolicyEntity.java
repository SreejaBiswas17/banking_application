package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "INSURANCE_POLICIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POLICY_ID")
    private Long policyId;

    @Column(name = "POLICY_NUMBER", nullable = false, unique = true, length = 30)
    private String policyNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "POLICY_TYPE", nullable = false, length = 50) // HEALTH, LIFE, TRAVEL, HOME
    private String policyType;

    @Column(name = "SUM_ASSURED", nullable = false, precision = 19, scale = 4)
    private BigDecimal sumAssured;

    @Column(name = "ANNUAL_PREMIUM", nullable = false, precision = 19, scale = 4)
    private BigDecimal annualPremium;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "STATUS", nullable = false, length = 20) // ACTIVE, LAPSED, CLAIMED
    private String status;

    @CreationTimestamp
    @Column(name = "ISSUED_AT", nullable = false, updatable = false)
    private LocalDateTime issuedAt;
}
