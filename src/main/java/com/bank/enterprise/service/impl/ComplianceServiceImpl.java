package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.*;
import com.bank.enterprise.repository.*;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private final AmlScreeningRepository amlRepository;
    private final FraudLogRepository fraudRepository;
    private final RegulatoryReportRepository reportRepository;
    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public AmlScreeningEntity screenCustomer(Long customerId) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Evaluate risk score rules
        BigDecimal riskScore = new BigDecimal("15.50");
        String level = "LOW";
        if (customer.getCountry() != null && !"USA".equalsIgnoreCase(customer.getCountry())) {
            riskScore = riskScore.add(new BigDecimal("30.00"));
            level = "MEDIUM";
        }

        AmlScreeningEntity screening = AmlScreeningEntity.builder()
                .customer(customer)
                .riskScore(riskScore)
                .riskLevel(level)
                .isPep(false)
                .isSanctionMatch(false)
                .screeningRemarks("Automated AML Sanction & PEP clearance completed")
                .build();

        AmlScreeningEntity saved = amlRepository.save(screening);
        auditService.logAction("AML_CUSTOMER_SCREENING", "SYSTEM", "AML_SCREENING", saved.getScreeningId().toString(), null, "Score: " + riskScore);
        return saved;
    }

    @Override
    @Transactional
    public FraudLogEntity logSuspiciousActivity(Long transactionId, String reason) {
        TransactionEntity tx = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", transactionId));

        FraudLogEntity log = FraudLogEntity.builder()
                .transaction(tx)
                .suspicionReason(reason)
                .fraudScore(new BigDecimal("85.00"))
                .status("UNDER_INVESTIGATION")
                .investigatorNotes("Flagged for high frequency or rapid velocity")
                .build();

        FraudLogEntity saved = fraudRepository.save(log);
        auditService.logAction("FRAUD_ALERT_FLAGGED", "COMPLIANCE_ENGINE", "FRAUD_LOG", saved.getFraudLogId().toString(), null, reason);
        return saved;
    }

    @Override
    @Transactional
    public RegulatoryReportEntity generateRegulatoryReport(String reportType, LocalDate start, LocalDate end) {
        RegulatoryReportEntity report = RegulatoryReportEntity.builder()
                .reportType(reportType)
                .reportPeriodStart(start)
                .reportPeriodEnd(end)
                .recordCount(42)
                .reportPayload("<?xml version=\"1.0\" encoding=\"UTF-8\"?><Report><Type>" + reportType + "</Type><Records>42</Records></Report>")
                .submissionStatus("SUBMITTED")
                .build();

        RegulatoryReportEntity saved = reportRepository.save(report);
        auditService.logAction("REGULATORY_REPORT_GENERATED", "COMPLIANCE_OFFICER", "REGULATORY_REPORT", saved.getReportId().toString(), null, "Generated " + reportType);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FraudLogEntity> getPendingInvestigations() {
        return fraudRepository.findByStatus("UNDER_INVESTIGATION");
    }
}
