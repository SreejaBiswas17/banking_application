package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "FEDWIRE_PAYMENTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FedWirePaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "WIRE_ID")
    private Long wireId;

    @Column(name = "IMAD_NUMBER", nullable = false, unique = true, length = 30) // Input Message Accountability Data
    private String imadNumber;

    @Column(name = "OMAD_NUMBER", nullable = false, unique = true, length = 30) // Output Message Accountability Data
    private String omadNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SENDER_ACCOUNT_ID", nullable = false)
    private AccountEntity senderAccount;

    @Column(name = "BENEFICIARY_ROUTING_NUMBER", nullable = false, length = 9)
    private String beneficiaryRoutingNumber;

    @Column(name = "BENEFICIARY_ACCOUNT_NUMBER", nullable = false, length = 34)
    private String beneficiaryAccountNumber;

    @Column(name = "BENEFICIARY_NAME", nullable = false, length = 150)
    private String beneficiaryName;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "STATUS", nullable = false, length = 20) // INITIATED, CLEARED, REJECTED
    private String status;

    @CreationTimestamp
    @Column(name = "PROCESSED_AT", nullable = false, updatable = false)
    private LocalDateTime processedAt;
}
