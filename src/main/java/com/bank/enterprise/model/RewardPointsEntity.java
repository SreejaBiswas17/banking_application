package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "REWARD_POINTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardPointsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REWARD_ID")
    private Long rewardId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false, unique = true)
    private CustomerEntity customer;

    @Column(name = "TOTAL_POINTS", nullable = false)
    @Builder.Default
    private Long totalPoints = 0L;

    @Column(name = "REDEEMED_POINTS", nullable = false)
    @Builder.Default
    private Long redeemedPoints = 0L;

    @UpdateTimestamp
    @Column(name = "LAST_UPDATED", nullable = false)
    private LocalDateTime lastUpdated;
}
