package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "GENERAL_LEDGER_ACCOUNTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneralLedgerAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GL_ID")
    private Long glId;

    @Column(name = "GL_CODE", nullable = false, unique = true, length = 20)
    private String glCode;

    @Column(name = "GL_NAME", nullable = false, length = 100)
    private String glName;

    @Column(name = "ACCOUNT_CATEGORY", nullable = false, length = 30) // ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
    private String accountCategory;

    @Column(name = "CURRENT_BALANCE", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal currentBalance = BigDecimal.ZERO;
}
