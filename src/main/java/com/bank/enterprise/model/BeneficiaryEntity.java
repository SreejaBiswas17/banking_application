package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BENEFICIARIES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BeneficiaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BENEFICIARY_ID")
    private Long beneficiaryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "BENEFICIARY_NAME", nullable = false, length = 100)
    private String beneficiaryName;

    @Column(name = "BENEFICIARY_ACCOUNT_NUMBER", nullable = false, length = 30)
    private String beneficiaryAccountNumber;

    @Column(name = "BANK_NAME", nullable = false, length = 100)
    private String bankName;

    @Column(name = "IFSC_OR_SWIFT", nullable = false, length = 20)
    private String ifscOrSwift;

    @Column(name = "MAX_TRANSFER_LIMIT", nullable = false, precision = 19, scale = 4)
    private BigDecimal maxTransferLimit;

    @Column(name = "IS_VERIFIED", nullable = false)
    @Builder.Default
    private Boolean isVerified = false;

    @CreationTimestamp
    @Column(name = "ADDED_AT", nullable = false, updatable = false)
    private LocalDateTime addedAt;
}
