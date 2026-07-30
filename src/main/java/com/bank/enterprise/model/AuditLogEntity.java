package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "AUDIT_LOGS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUDIT_ID")
    private Long auditId;

    @Column(name = "ACTION", nullable = false, length = 100)
    private String action;

    @Column(name = "PERFORMED_BY", nullable = false, length = 50)
    private String performedBy;

    @Column(name = "ENTITY_NAME", nullable = false, length = 50)
    private String entityName;

    @Column(name = "ENTITY_ID", length = 50)
    private String entityId;

    @Lob
    @Column(name = "OLD_STATE")
    private String oldState;

    @Lob
    @Column(name = "NEW_STATE")
    private String newState;

    @Column(name = "IP_ADDRESS", length = 45)
    private String ipAddress;

    @CreationTimestamp
    @Column(name = "TIMESTAMP", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}
