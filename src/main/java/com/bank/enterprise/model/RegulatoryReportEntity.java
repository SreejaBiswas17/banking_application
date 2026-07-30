package com.bank.enterprise.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "REGULATORY_REPORTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegulatoryReportEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REPORT_ID")
    private Long reportId;

    @Column(name = "REPORT_TYPE", nullable = false, length = 50) // STR (Suspicious Transaction Report), CTR (Currency Transaction Report)
    private String reportType;

    @Column(name = "REPORT_PERIOD_START", nullable = false)
    private LocalDate reportPeriodStart;

    @Column(name = "REPORT_PERIOD_END", nullable = false)
    private LocalDate reportPeriodEnd;

    @Column(name = "RECORD_COUNT", nullable = false)
    private Integer recordCount;

    @Lob
    @Column(name = "REPORT_PAYLOAD")
    private String reportPayload;

    @Column(name = "SUBMISSION_STATUS", nullable = false, length = 20) // DRAFT, SUBMITTED, ACKNOWLEDGED, REJECTED
    private String submissionStatus;

    @CreationTimestamp
    @Column(name = "GENERATED_AT", nullable = false, updatable = false)
    private LocalDateTime generatedAt;
}
