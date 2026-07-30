package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.MoneyMarketDealEntity;
import com.bank.enterprise.model.TreasuryBondEntity;
import com.bank.enterprise.service.TreasuryManagementService;
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
@RequestMapping("/treasury")
@RequiredArgsConstructor
@Tag(name = "Treasury & Interbank Operations", description = "Endpoints for Sovereign/Corporate Bonds & Interbank Money Market Deals")
public class TreasuryController {

    private final TreasuryManagementService treasuryService;

    @PostMapping("/bonds/purchase")
    @Operation(summary = "Purchase Treasury/Sovereign Bond into Bank Portfolio")
    public ResponseEntity<ApiResponse<TreasuryBondEntity>> purchaseBond(
            @RequestParam String isin,
            @RequestParam String issuerName,
            @RequestParam BigDecimal faceValue,
            @RequestParam BigDecimal couponRate,
            @RequestParam Currency currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maturityDate,
            @RequestParam String holdingType) {
        return ResponseEntity.ok(ApiResponse.success(treasuryService.purchaseBond(isin, issuerName, faceValue, couponRate, currency, maturityDate, holdingType), "Bond purchased"));
    }

    @PostMapping("/money-market/deal")
    @Operation(summary = "Execute interbank money market lending/borrowing deal")
    public ResponseEntity<ApiResponse<MoneyMarketDealEntity>> executeDeal(
            @RequestParam String counterpartyBank,
            @RequestParam String dealType,
            @RequestParam BigDecimal principalAmount,
            @RequestParam BigDecimal interestRate,
            @RequestParam Currency currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maturityDate) {
        return ResponseEntity.ok(ApiResponse.success(treasuryService.executeInterbankDeal(counterpartyBank, dealType, principalAmount, interestRate, currency, startDate, maturityDate), "Deal executed"));
    }

    @GetMapping("/bonds")
    @Operation(summary = "Get all Treasury Bond holdings")
    public ResponseEntity<ApiResponse<List<TreasuryBondEntity>>> getBonds() {
        return ResponseEntity.ok(ApiResponse.success(treasuryService.getAllBonds()));
    }
}
