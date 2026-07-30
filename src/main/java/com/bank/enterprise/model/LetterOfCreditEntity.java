package com.bank.enterprise.model;

import com.bank.enterprise.common.Currency;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LETTERS_OF_CREDIT")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LetterOfCreditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LC_ID")
    private Long lcId;

    @Column(name = "LC_NUMBER", nullable = false, unique = true, length = 30)
    private String lcNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICANT_CUSTOMER_ID", nullable = false)
    private CustomerEntity applicantCustomer;

    @Column(name = "BENEFICIARY_NAME", nullable = false, length = 100)
    private String beneficiaryName;

    @Column(name = "ADVISING_BANK_SWIFT", nullable = false, length = 20)
    private String advisingBankSwift;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "CURRENCY", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "EXPIRY_DATE", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "STATUS", nullable = false, length = 20) // ISSUED, ADVISED, DISCHARGED, EXPIRED
    private String status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
