package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import com.bank.enterprise.service.CorporateBankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/corporate")
@RequiredArgsConstructor
@Tag(name = "Corporate & Commercial Banking", description = "Endpoints for Corporate Accounts & Bulk Salary Payroll Batch Execution")
public class CorporateBankingController {

    private final CorporateBankingService corporateService;

    @PostMapping("/register")
    @Operation(summary = "Register a new corporate enterprise account")
    public ResponseEntity<ApiResponse<CorporateAccountEntity>> registerCorporate(
            @RequestParam String companyName,
            @RequestParam String registrationNumber,
            @RequestParam String taxId,
            @RequestParam Long primaryAccountId,
            @RequestParam BigDecimal creditLimit) {
        return ResponseEntity.ok(ApiResponse.success(corporateService.registerCorporateAccount(companyName, registrationNumber, taxId, primaryAccountId, creditLimit), "Corporate registered"));
    }

    @PostMapping("/payroll/execute")
    @Operation(summary = "Execute automated bulk payroll batch disbursement")
    public ResponseEntity<ApiResponse<PayrollBatchEntity>> executePayroll(
            @RequestParam Long corporateId,
            @RequestParam Integer totalEmployees,
            @RequestParam BigDecimal totalAmount) {
        return ResponseEntity.ok(ApiResponse.success(corporateService.executePayrollBatch(corporateId, totalEmployees, totalAmount), "Payroll executed successfully"));
    }

    @GetMapping("/payroll/history/{corporateId}")
    @Operation(summary = "Get historical payroll batches for corporate entity")
    public ResponseEntity<ApiResponse<List<PayrollBatchEntity>>> getPayrollHistory(@PathVariable Long corporateId) {
        return ResponseEntity.ok(ApiResponse.success(corporateService.getCorporatePayrollHistory(corporateId)));
    }
}
