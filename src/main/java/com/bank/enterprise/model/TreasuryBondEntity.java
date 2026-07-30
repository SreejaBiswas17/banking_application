package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "TREASURY_BONDS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TreasuryBondEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BOND_ID")
    private Long bondId;

    @Column(name = "ISIN", nullable = false, unique = true, length = 20)
    private String isin;

    @Column(name = "ISSUER_NAME", nullable = false, length = 100)
    private String issuerName;

    @Column(name = "FACE_VALUE", nullable = false, precision = 19, scale = 4)
    private BigDecimal faceValue;

    @Column(name = "COUPON_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal couponRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "MATURITY_DATE", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "HOLDING_TYPE", nullable = false, length = 20) // HFT (Held for Trading), AFS (Available for Sale), HTM (Held to Maturity)
    private String holdingType;

    @CreationTimestamp
    @Column(name = "PURCHASED_AT", nullable = false, updatable = false)
    private LocalDateTime purchasedAt;
}
