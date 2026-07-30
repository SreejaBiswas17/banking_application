package com.bank.enterprise.service;

import com.bank.enterprise.model.AmlScreeningEntity;
import com.bank.enterprise.model.FraudLogEntity;
import com.bank.enterprise.model.RegulatoryReportEntity;

import java.time.LocalDate;
import java.util.List;

public interface ComplianceService {
    AmlScreeningEntity screenCustomer(Long customerId);
    FraudLogEntity logSuspiciousActivity(Long transactionId, String reason);
    RegulatoryReportEntity generateRegulatoryReport(String reportType, LocalDate start, LocalDate end);
    List<FraudLogEntity> getPendingInvestigations();
}
