package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "SECURITIES_PORTFOLIOS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecuritiesPortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PORTFOLIO_ID")
    private Long portfolioId;

    @Column(name = "DEMAT_ACCOUNT_NUMBER", nullable = false, unique = true, length = 30)
    private String dematAccountNumber;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "TOTAL_PORTFOLIO_VALUE", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal totalPortfolioValue = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "OPENED_AT", nullable = false, updatable = false)
    private LocalDateTime openedAt;
}
