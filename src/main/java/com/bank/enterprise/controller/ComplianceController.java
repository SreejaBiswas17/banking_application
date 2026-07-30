package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.AmlScreeningEntity;
import com.bank.enterprise.model.FraudLogEntity;
import com.bank.enterprise.model.RegulatoryReportEntity;
import com.bank.enterprise.service.ComplianceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/compliance")
@RequiredArgsConstructor
@Tag(name = "AML & Compliance", description = "Endpoints for Anti-Money Laundering Screening & Regulatory Filings")
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping("/screen/{customerId}")
    @Operation(summary = "Screen customer against AML sanction lists")
    public ResponseEntity<ApiResponse<AmlScreeningEntity>> screenCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.screenCustomer(customerId)));
    }

    @PostMapping("/fraud-flag")
    @Operation(summary = "Flag transaction for suspicious activity investigation")
    public ResponseEntity<ApiResponse<FraudLogEntity>> flagFraud(@RequestParam Long transactionId, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.logSuspiciousActivity(transactionId, reason)));
    }

    @GetMapping("/fraud/pending")
    @Operation(summary = "Get all transactions currently under fraud investigation")
    public ResponseEntity<ApiResponse<List<FraudLogEntity>>> getPendingFraud() {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getPendingInvestigations()));
    }

    @PostMapping("/reports/generate")
    @Operation(summary = "Generate regulatory compliance report (STR / CTR)")
    public ResponseEntity<ApiResponse<RegulatoryReportEntity>> generateReport(
            @RequestParam String reportType,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.generateRegulatoryReport(reportType, startDate, endDate)));
    }
}
