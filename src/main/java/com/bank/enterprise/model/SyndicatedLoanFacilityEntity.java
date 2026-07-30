package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SYNDICATED_LOAN_FACILITIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyndicatedLoanFacilityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FACILITY_ID")
    private Long facilityId;

    @Column(name = "FACILITY_NUMBER", nullable = false, unique = true, length = 30)
    private String facilityNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BORROWER_CORPORATE_ID", nullable = false)
    private CorporateAccountEntity borrowerCorporate;

    @Column(name = "TOTAL_FACILITY_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalFacilityAmount;

    @Column(name = "LEAD_ARRANGER_BANK", nullable = false, length = 100)
    private String leadArrangerBank;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "MATURITY_DATE", nullable = false)
    private LocalDate maturityDate;

    @Column(name = "STATUS", nullable = false, length = 20) // SYNDICATING, ACTIVE, FULLY_DRAWN, CLOSED
    private String status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
