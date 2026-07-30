package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "FIXED_DEPOSITS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FixedDepositEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FD_ID")
    private Long fdId;

    @Column(name = "FD_NUMBER", nullable = false, unique = true, length = 20)
    private String fdNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LINKED_ACCOUNT_ID", nullable = false)
    private AccountEntity linkedAccount;

    @Column(name = "PRINCIPAL_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "INTEREST_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "TENURE_DAYS", nullable = false)
    private Integer tenureDays;

    @Column(name = "MATURITY_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal maturityAmount;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "MATURITY_DATE", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "IS_CLOSED", nullable = false)
    @Builder.Default
    private Boolean isClosed = false;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
