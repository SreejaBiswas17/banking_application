package com.bank.enterprise.model;

import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.common.LoanType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "LOANS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOAN_ID")
    private Long loanId;

    @Column(name = "LOAN_NUMBER", nullable = false, unique = true, length = 20)
    private String loanNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Enumerated(EnumType.STRING)
    @Column(name = "LOAN_TYPE", nullable = false, length = 30)
    private LoanType loanType;

    @Column(name = "PRINCIPAL_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalAmount;

    @Column(name = "OUTSTANDING_PRINCIPAL", nullable = false, precision = 19, scale = 4)
    private BigDecimal outstandingPrincipal;

    @Column(name = "ANNUAL_INTEREST_RATE", nullable = false, precision = 5, scale = 4)
    private BigDecimal annualInterestRate;

    @Column(name = "TENURE_MONTHS", nullable = false)
    private Integer tenureMonths;

    @Column(name = "MONTHLY_EMI", nullable = false, precision = 19, scale = 4)
    private BigDecimal monthlyEmi;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private LoanStatus status = LoanStatus.SUBMITTED;

    @Column(name = "DISBURSEMENT_DATE")
    private LocalDate disbursementDate;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
