package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "PARTICIPANT_BANKS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParticipantBankEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PARTICIPANT_ID")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FACILITY_ID", nullable = false)
    private SyndicatedLoanFacilityEntity facility;

    @Column(name = "BANK_NAME", nullable = false, length = 100)
    private String bankName;

    @Column(name = "SWIFT_BIC", nullable = false, length = 11)
    private String swiftBic;

    @Column(name = "COMMITTED_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal committedAmount;

    @Column(name = "PARTICIPATION_SHARE_PERCENT", nullable = false, precision = 5, scale = 4)
    private BigDecimal participationSharePercent;
}
