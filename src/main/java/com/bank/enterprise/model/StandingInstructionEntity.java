package com.bank.enterprise.model;

import com.bank.enterprise.common.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "STANDING_INSTRUCTIONS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandingInstructionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "INSTRUCTION_ID")
    private Long instructionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_ACCOUNT_ID", nullable = false)
    private AccountEntity sourceAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DESTINATION_ACCOUNT_ID", nullable = false)
    private AccountEntity destinationAccount;

    @Column(name = "AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "TRANSACTION_TYPE", nullable = false, length = 30)
    private TransactionType transactionType;

    @Column(name = "EXECUTION_DAY_OF_MONTH", nullable = false)
    private Integer executionDayOfMonth;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "LAST_EXECUTION_DATE")
    private LocalDate lastExecutionDate;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
