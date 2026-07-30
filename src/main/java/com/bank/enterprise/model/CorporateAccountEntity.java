package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "CORPORATE_ACCOUNTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporateAccountEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CORPORATE_ID")
    private Long corporateId;

    @Column(name = "COMPANY_NAME", nullable = false, length = 150)
    private String companyName;

    @Column(name = "REGISTRATION_NUMBER", nullable = false, unique = true, length = 50)
    private String registrationNumber;

    @Column(name = "TAX_ID", nullable = false, unique = true, length = 50)
    private String taxId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRIMARY_ACCOUNT_ID", nullable = false)
    private AccountEntity primaryAccount;

    @Column(name = "CREDIT_LINE_LIMIT", nullable = false, precision = 19, scale = 4)
    private BigDecimal creditLineLimit;

    @CreationTimestamp
    @Column(name = "INCORPORATED_AT", nullable = false, updatable = false)
    private LocalDateTime incorporatedAt;
}
