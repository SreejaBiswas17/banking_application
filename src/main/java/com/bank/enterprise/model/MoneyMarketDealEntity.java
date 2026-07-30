package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "MONEY_MARKET_DEALS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyMarketDealEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DEAL_ID")
    private Long dealId;

    @Column(name = "DEAL_REFERENCE", nullable = false, unique = true, length = 30)
    private String dealReference;

    @Column(name = "COUNTERPARTY_BANK", nullable = false, length = 100)
    private String counterpartyBank;

    @Column(name = "DEAL_TYPE", nullable = false, length = 20) // INTERBANK_LENDING, INTERBANK_BORROWING, REPO, REVERSE_REPO
    private String dealType;

    @Column(name = "PRINCIPAL_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "INTEREST_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "MATURITY_DATE", nullable = false)
    private LocalDate maturityDate;

    @CreationTimestamp
    @Column(name = "DEAL_TIME", nullable = false, updatable = false)
    private LocalDateTime dealTime;
}
