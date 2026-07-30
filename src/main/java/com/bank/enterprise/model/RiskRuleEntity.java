package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "RISK_RULES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RiskRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RULE_ID")
    private Long ruleId;

    @Column(name = "RULE_NAME", nullable = false, unique = true, length = 100)
    private String ruleName;

    @Column(name = "THRESHOLD_AMOUNT", nullable = false, precision = 19, scale = 4)
    private BigDecimal thresholdAmount;

    @Column(name = "ACTION_TYPE", nullable = false, length = 30) // FLAG_AUDIT, REJECT, SUSPEND_ACCOUNT
    private String actionType;

    @Column(name = "IS_ACTIVE", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
