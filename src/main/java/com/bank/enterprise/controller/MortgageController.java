package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.MortgagePropertyEntity;
import com.bank.enterprise.service.MortgageUnderwritingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/mortgages")
@RequiredArgsConstructor
@Tag(name = "Mortgage Underwriting", description = "Endpoints for Property Appraisals & Loan-to-Value (LTV) Ratios")
public class MortgageController {

    private final MortgageUnderwritingService mortgageService;

    @PostMapping("/properties/register")
    @Operation(summary = "Register mortgaged real estate property collateral")
    public ResponseEntity<ApiResponse<MortgagePropertyEntity>> registerProperty(
            @RequestParam Long loanId,
            @RequestParam String propertyAddress,
            @RequestParam BigDecimal appraisedValue,
            @RequestParam String propertyType) {
        return ResponseEntity.ok(ApiResponse.success(mortgageService.registerMortgageProperty(loanId, propertyAddress, appraisedValue, propertyType), "Mortgage property collateral registered"));
    }
}
