package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SUPPORT_TICKETS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportTicketEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TICKET_ID")
    private Long ticketId;

    @Column(name = "TICKET_NUMBER", nullable = false, unique = true, length = 30)
    private String ticketNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private CustomerEntity customer;

    @Column(name = "SUBJECT", nullable = false, length = 150)
    private String subject;

    @Column(name = "CATEGORY", nullable = false, length = 50) // ACCOUNT_QUERY, TRANSACTION_DISPUTE, CARD_ISSUE, LOAN_QUERY, TECHNICAL
    private String category;

    @Column(name = "PRIORITY", nullable = false, length = 20) // LOW, MEDIUM, HIGH, URGENT
    private String priority;

    @Column(name = "STATUS", nullable = false, length = 20) // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    private String status;

    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<TicketMessageEntity> messages = new ArrayList<>();
}
