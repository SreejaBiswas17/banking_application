package com.bank.enterprise.model;

import com.bank.enterprise.common.KycStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CUSTOMERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CUSTOMER_ID")
    private Long customerId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private UserEntity user;

    @Column(name = "FIRST_NAME", nullable = false, length = 50)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false, length = 50)
    private String lastName;

    @Column(name = "DATE_OF_BIRTH", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "TAX_IDENTIFICATION_NUMBER", nullable = false, unique = true, length = 30)
    private String taxIdNumber;

    @Column(name = "NATIONAL_ID", nullable = false, unique = true, length = 30)
    private String nationalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "KYC_STATUS", nullable = false, length = 20)
    @Builder.Default
    private KycStatus kycStatus = KycStatus.PENDING;

    @Column(name = "ADDRESS_LINE1", nullable = false, length = 100)
    private String addressLine1;

    @Column(name = "ADDRESS_LINE2", length = 100)
    private String addressLine2;

    @Column(name = "CITY", nullable = false, length = 50)
    private String city;

    @Column(name = "STATE", nullable = false, length = 50)
    private String state;

    @Column(name = "POSTAL_CODE", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "COUNTRY", nullable = false, length = 50)
    @Builder.Default
    private String country = "USA";

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AccountEntity> accounts = new ArrayList<>();
}
