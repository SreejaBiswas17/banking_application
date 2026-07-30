package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;
import com.bank.enterprise.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/investments")
@RequiredArgsConstructor
@Tag(name = "Mutual Funds & Investments", description = "Endpoints for Wealth Building & Mutual Fund Unit Purchases")
public class InvestmentController {

    private final InvestmentService investmentService;

    @GetMapping("/schemes")
    @Operation(summary = "Get list of available Mutual Fund schemes")
    public ResponseEntity<ApiResponse<List<MutualFundSchemeEntity>>> getSchemes() {
        return ResponseEntity.ok(ApiResponse.success(investmentService.getAvailableSchemes()));
    }

    @PostMapping("/buy")
    @Operation(summary = "Purchase Mutual Fund units from linked bank account")
    public ResponseEntity<ApiResponse<MutualFundHoldingEntity>> buyUnits(
            @RequestParam Long customerId,
            @RequestParam Long accountId,
            @RequestParam String schemeCode,
            @RequestParam BigDecimal amount) {
        return ResponseEntity.ok(ApiResponse.success(investmentService.buyMutualFundUnits(customerId, accountId, schemeCode, amount), "Units purchased"));
    }

    @GetMapping("/portfolio/{customerId}")
    @Operation(summary = "Get customer investment portfolio")
    public ResponseEntity<ApiResponse<List<MutualFundHoldingEntity>>> getPortfolio(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(investmentService.getCustomerPortfolio(customerId)));
    }
}
