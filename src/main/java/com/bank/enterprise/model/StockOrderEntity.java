package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "STOCK_ORDERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ORDER_ID")
    private Long orderId;

    @Column(name = "ORDER_REFERENCE", nullable = false, unique = true, length = 30)
    private String orderReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PORTFOLIO_ID", nullable = false)
    private SecuritiesPortfolioEntity portfolio;

    @Column(name = "TICKER_SYMBOL", nullable = false, length = 10)
    private String tickerSymbol;

    @Column(name = "ORDER_TYPE", nullable = false, length = 10) // BUY, SELL
    private String orderType;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    @Column(name = "EXECUTION_PRICE", nullable = false, precision = 19, scale = 4)
    private BigDecimal executionPrice;

    @Column(name = "STATUS", nullable = false, length = 20) // PENDING, EXECUTED, CANCELLED
    private String status;

    @CreationTimestamp
    @Column(name = "PLACED_AT", nullable = false, updatable = false)
    private LocalDateTime placedAt;
}
