package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "TICKET_MESSAGES")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MESSAGE_ID")
    private Long messageId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TICKET_ID", nullable = false)
    private SupportTicketEntity ticket;

    @Column(name = "SENDER_NAME", nullable = false, length = 50)
    private String senderName;

    @Column(name = "SENDER_ROLE", nullable = false, length = 20) // CUSTOMER, SUPPORT_AGENT, SYSTEM
    private String senderRole;

    @Column(name = "MESSAGE_TEXT", nullable = false, length = 2000)
    private String messageText;

    @CreationTimestamp
    @Column(name = "SENT_AT", nullable = false, updatable = false)
    private LocalDateTime sentAt;
}
