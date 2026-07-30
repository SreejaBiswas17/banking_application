package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "EXCHANGE_RATES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RATE_ID")
    private Long rateId;

    @Enumerated(EnumType.STRING)
    @Column(name = "BASE_CURRENCY", nullable = false, length = 3)
    private Currency baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_CURRENCY", nullable = false, length = 3)
    private Currency targetCurrency;

    @Column(name = "EXCHANGE_RATE", nullable = false, precision = 12, scale = 6)
    private BigDecimal exchangeRate;

    @UpdateTimestamp
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
}
