package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "JOURNAL_LINE_ITEMS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalLineItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LINE_ID")
    private Long lineId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOURNAL_ID", nullable = false)
    private JournalEntryEntity journalEntry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GL_ID", nullable = false)
    private GeneralLedgerAccountEntity glAccount;

    @Column(name = "DEBIT_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal debitAmount = BigDecimal.ZERO;

    @Column(name = "CREDIT_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;
}
