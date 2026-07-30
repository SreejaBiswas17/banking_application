package com.bank.enterprise.model;

import com.bank.enterprise.common.EmiStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "EMI_SCHEDULES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmiScheduleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOAN_ID", nullable = false)
    private LoanEntity loan;

    @Column(name = "INSTALLMENT_NUMBER", nullable = false)
    private Integer installmentNumber;

    @Column(name = "DUE_DATE", nullable = false)
    private LocalDate dueDate;

    @Column(name = "PRINCIPAL_COMPONENT", nullable = false, precision = 19, scale = 4)
    private BigDecimal principalComponent;

    @Column(name = "INTEREST_COMPONENT", nullable = false, precision = 19, scale = 4)
    private BigDecimal interestComponent;

    @Column(name = "TOTAL_EMI_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal totalEmiAmount;

    @Column(name = "PAID_AMOUNT", nullable = false, precision = 19, scale = 4)
    @Builder.Default
    private BigDecimal paidAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    @Builder.Default
    private EmiStatus status = EmiStatus.UNPAID;

    @Column(name = "PAYMENT_DATE")
    private LocalDate paymentDate;
}
