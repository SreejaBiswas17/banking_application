package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "PAYROLL_BATCHES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PayrollBatchEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BATCH_ID")
    private Long batchId;

    @Column(name = "BATCH_REFERENCE", nullable = false, unique = true, length = 30)
    private String batchReference;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CORPORATE_ID", nullable = false)
    private CorporateAccountEntity corporateAccount;

    @Column(name = "TOTAL_EMPLOYEES", nullable = false)
    private Integer totalEmployees;

    @Column(name = "TOTAL_PAYROLL_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPayrollAmount;

    @Column(name = "STATUS", nullable = false, length = 20) // SUBMITTED, PROCESSING, COMPLETED, FAILED
    private String status;

    @CreationTimestamp
    @Column(name = "EXECUTED_AT", nullable = false, updatable = false)
    private LocalDateTime executedAt;
}
