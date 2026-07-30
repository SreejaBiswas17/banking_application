package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "MUTUAL_FUND_HOLDINGS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MutualFundHoldingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HOLDING_ID")
    private Long holdingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SCHEME_ID", nullable = false)
    private MutualFundSchemeEntity scheme;

    @Column(name = "TOTAL_UNITS", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalUnits;

    @Column(name = "AVERAGE_PURCHASE_NAV", nullable = false, precision = 12, scale = 4)
    private BigDecimal averagePurchaseNav;

    @Column(name = "TOTAL_INVESTED_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalInvestedAmount;

    @CreationTimestamp
    @Column(name = "FIRST_INVESTED_AT", nullable = false, updatable = false)
    private LocalDateTime firstInvestedAt;
}
