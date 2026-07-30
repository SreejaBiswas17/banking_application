package com.bank.enterprise.model;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.common.CardType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CARDS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private Long cardId;

    @Column(name = "CARD_NUMBER_MASKED", nullable = false, length = 19)
    private String cardNumberMasked;

    @Column(name = "CARD_HASH", nullable = false, unique = true, length = 100)
    private String cardHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private AccountEntity account;

    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_TYPE", nullable = false, length = 20)
    private CardType cardType;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "CVV_HASH", nullable = false, length = 100)
    private String cvvHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "CARD_STATUS", nullable = false, length = 20)
    @Builder.Default
    private CardStatus cardStatus = CardStatus.INACTIVE;

    @Column(name = "DAILY_ATM_LIMIT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal dailyAtmLimit = new BigDecimal("1000.00");

    @Column(name = "DAILY_POS_LIMIT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal dailyPosLimit = new BigDecimal("5000.00");

    @Column(name = "CREDIT_LIMIT", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal creditLimit = BigDecimal.ZERO;

    @Column(name = "USED_CREDIT", precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal usedCredit = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
