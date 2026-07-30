package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.InsurancePolicyEntity;
import com.bank.enterprise.service.BancassuranceService;
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
@RequestMapping("/bancassurance")
@RequiredArgsConstructor
@Tag(name = "Bancassurance & Insurance Products", description = "Endpoints for Insurance Policy Issuance & Life/Health Cover")
public class BancassuranceController {

    private final BancassuranceService bancassuranceService;

    @PostMapping("/policies/issue")
    @Operation(summary = "Issue a new insurance policy for bank customer")
    public ResponseEntity<ApiResponse<InsurancePolicyEntity>> issuePolicy(
            @RequestParam Long customerId,
            @RequestParam String policyType,
            @RequestParam BigDecimal sumAssured,
            @RequestParam BigDecimal annualPremium,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate expiryDate) {
        return ResponseEntity.ok(ApiResponse.success(bancassuranceService.issuePolicy(customerId, policyType, sumAssured, annualPremium, expiryDate), "Policy issued"));
    }

    @GetMapping("/policies/customer/{customerId}")
    @Operation(summary = "Get all insurance policies for customer")
    public ResponseEntity<ApiResponse<List<InsurancePolicyEntity>>> getCustomerPolicies(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(bancassuranceService.getCustomerPolicies(customerId)));
    }
}
