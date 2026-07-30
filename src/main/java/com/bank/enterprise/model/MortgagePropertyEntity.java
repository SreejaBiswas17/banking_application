package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MORTGAGE_PROPERTIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MortgagePropertyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROPERTY_ID")
    private Long propertyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAN_ID", nullable = false, unique = true)
    private LoanEntity loan;

    @Column(name = "PROPERTY_ADDRESS", nullable = false, length = 200)
    private String propertyAddress;

    @Column(name = "APPRAISED_VALUE", nullable = false, precision = 19, scale = 4)
    private BigDecimal appraisedValue;

    @Column(name = "LTV_RATIO", nullable = false, precision = 5, scale = 4) // Loan-to-Value Ratio
    private BigDecimal ltvRatio;

    @Column(name = "PROPERTY_TYPE", nullable = false, length = 50) // SINGLE_FAMILY, CONDO, COMMERCIAL
    private String propertyType;

    @CreationTimestamp
    @Column(name = "APPRAISED_AT", nullable = false, updatable = false)
    private LocalDateTime appraisedAt;
}
