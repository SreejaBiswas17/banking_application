package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ESCROW_AGREEMENTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EscrowAgreementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ESCROW_ID")
    private Long escrowId;

    @Column(name = "ESCROW_NUMBER", nullable = false, unique = true, length = 30)
    private String escrowNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BUYER_CUSTOMER_ID", nullable = false)
    private CustomerEntity buyerCustomer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SELLER_CUSTOMER_ID", nullable = false)
    private CustomerEntity sellerCustomer;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ESCROW_ACCOUNT_ID", nullable = false)
    private AccountEntity escrowAccount;

    @Column(name = "TOTAL_ESCROW_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalEscrowAmount;

    @Column(name = "RELEASED_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal releasedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "STATUS", nullable = false, length = 20) // FUNDED, MILESTONE_RELEASE, COMPLETED, DISPUTED
    private String status;

    @CreationTimestamp
    @Column(name = "ESTABLISHED_AT", nullable = false, updatable = false)
    private LocalDateTime establishedAt;
}
