package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.dto.ReportDto;
import com.bank.enterprise.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
@Tag(name = "Analytics & Reports", description = "Endpoints for Executive Financial Summaries & Statements")
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial-summary")
    @Operation(summary = "Generate bank-wide executive financial summary")
    public ResponseEntity<ApiResponse<ReportDto.FinancialSummaryDto>> getFinancialSummary() {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateFinancialSummary()));
    }

    @GetMapping("/account-statement/{accountNumber}")
    @Operation(summary = "Generate account statement for date range")
    public ResponseEntity<ApiResponse<ReportDto.AccountStatementDto>> getAccountStatement(
            @PathVariable String accountNumber,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(reportService.generateAccountStatement(accountNumber, startDate, endDate)));
    }
}
