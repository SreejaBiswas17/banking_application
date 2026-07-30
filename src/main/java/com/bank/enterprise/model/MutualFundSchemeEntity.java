package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MUTUAL_FUND_SCHEMES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutualFundSchemeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEME_ID")
    private Long schemeId;

    @Column(name = "SCHEME_CODE", nullable = false, unique = true, length = 30)
    private String schemeCode;

    @Column(name = "SCHEME_NAME", nullable = false, length = 150)
    private String schemeName;

    @Column(name = "CATEGORY", nullable = false, length = 50) // EQUITY, DEBT, HYBRID, INDEX
    private String category;

    @Column(name = "CURRENT_NAV", nullable = false, precision = 12, scale = 4)
    private BigDecimal currentNav;

    @Column(name = "RISK_RATING", nullable = false, length = 20) // LOW, MODERATE, HIGH, VERY_HIGH
    private String riskRating;

    @UpdateTimestamp
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
}
