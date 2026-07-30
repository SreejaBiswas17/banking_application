package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CURRENCY_RATE_HISTORY")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HISTORY_ID")
    private Long historyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "BASE_CURRENCY", nullable = false, length = 3)
    private Currency baseCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "TARGET_CURRENCY", nullable = false, length = 3)
    private Currency targetCurrency;

    @Column(name = "BID_RATE", nullable = false, precision = 12, scale = 6)
    private BigDecimal bidRate;

    @Column(name = "ASK_RATE", nullable = false, precision = 12, scale = 6)
    private BigDecimal askRate;

    @CreationTimestamp
    @Column(name = "RECORDED_AT", nullable = false, updatable = false)
    private LocalDateTime recordedAt;
}
