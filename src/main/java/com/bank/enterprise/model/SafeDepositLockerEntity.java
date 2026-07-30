package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "SAFE_DEPOSIT_LOCKERS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SafeDepositLockerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCKER_ID")
    private Long lockerId;

    @Column(name = "LOCKER_NUMBER", nullable = false, unique = true, length = 20)
    private String lockerNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID")
    private CustomerEntity customer;

    @Column(name = "LOCKER_SIZE", nullable = false, length = 20) // SMALL, MEDIUM, LARGE, EXTRA_LARGE
    private String lockerSize;

    @Column(name = "ANNUAL_RENT", nullable = false, precision = 19, scale = 4)
    private BigDecimal annualRent;

    @Column(name = "STATUS", nullable = false, length = 20) // VACANT, RENTED, MAINTENANCE
    private String status;

    @Column(name = "RENTAL_EXPIRY_DATE")
    private LocalDate rentalExpiryDate;

    @CreationTimestamp
    @Column(name = "REGISTERED_AT", nullable = false, updatable = false)
    private LocalDateTime registeredAt;
}
