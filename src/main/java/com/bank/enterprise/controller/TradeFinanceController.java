package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.LetterOfCreditEntity;
import com.bank.enterprise.service.TradeFinanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/trade-finance")
@RequiredArgsConstructor
@Tag(name = "Trade Finance & Commercial Banking", description = "Endpoints for Letters of Credit & Import/Export Operations")
public class TradeFinanceController {

    private final TradeFinanceService tradeFinanceService;

    @PostMapping("/lc/issue")
    @Operation(summary = "Issue a commercial Letter of Credit (LC)")
    public ResponseEntity<ApiResponse<LetterOfCreditEntity>> issueLc(
            @RequestParam Long customerId,
            @RequestParam String beneficiaryName,
            @RequestParam String advisingBankSwift,
            @RequestParam BigDecimal amount,
            @RequestParam Currency currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        return ResponseEntity.ok(ApiResponse.success(tradeFinanceService.issueLetterOfCredit(customerId, beneficiaryName, advisingBankSwift, amount, currency, expiryDate), "LC Issued"));
    }

    @GetMapping("/lc/{lcNumber}")
    @Operation(summary = "Get Letter of Credit by LC Number")
    public ResponseEntity<ApiResponse<LetterOfCreditEntity>> getLcByNumber(@PathVariable String lcNumber) {
        return ResponseEntity.ok(ApiResponse.success(tradeFinanceService.getLcByNumber(lcNumber)));
    }

    @GetMapping("/lc/customer/{customerId}")
    @Operation(summary = "Get all Letters of Credit for customer")
    public ResponseEntity<ApiResponse<List<LetterOfCreditEntity>>> getCustomerLcs(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(tradeFinanceService.getCustomerLcs(customerId)));
    }

    @PostMapping("/lc/{lcId}/discharge")
    @Operation(summary = "Discharge completed Letter of Credit")
    public ResponseEntity<ApiResponse<String>> dischargeLc(@PathVariable Long lcId) {
        tradeFinanceService.dischargeLc(lcId);
        return ResponseEntity.ok(ApiResponse.success("LC discharged successfully"));
    }
}
