package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "JOURNAL_ENTRIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalEntryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "JOURNAL_ID")
    private Long journalId;

    @Column(name = "JOURNAL_NUMBER", nullable = false, unique = true, length = 30)
    private String journalNumber;

    @Column(name = "NARRATION", nullable = false, length = 255)
    private String narration;

    @Column(name = "TOTAL_DEBIT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalDebit;

    @Column(name = "TOTAL_CREDIT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalCredit;

    @CreationTimestamp
    @Column(name = "POSTED_AT", nullable = false, updatable = false)
    private LocalDateTime postedAt;

    @OneToMany(mappedBy = "journalEntry", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<JournalLineItemEntity> lineItems = new ArrayList<>();
}
