package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FOREX_CONTRACTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForexContractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTRACT_ID")
    private Long contractId;

    @Column(name = "CONTRACT_NUMBER", nullable = false, unique = true, length = 30)
    private String contractNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "BUY_CURRENCY", nullable = false, length = 3)
    private Currency buyCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "SELL_CURRENCY", nullable = false, length = 3)
    private Currency sellCurrency;

    @Column(name = "BUY_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal buyAmount;

    @Column(name = "SELL_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal sellAmount;

    @Column(name = "APPLIED_RATE", nullable = false, precision = 12, scale = 6)
    private BigDecimal appliedRate;

    @Column(name = "VALUE_DATE", nullable = false)
    private LocalDate valueDate;

    @Column(name = "STATUS", nullable = false, length = 20) // BOOKED, SETTLED, CANCELLED
    private String status;

    @CreationTimestamp
    @Column(name = "BOOKED_AT", nullable = false, updatable = false)
    private LocalDateTime bookedAt;
}
