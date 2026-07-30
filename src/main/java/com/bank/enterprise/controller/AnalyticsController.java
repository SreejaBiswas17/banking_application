package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Intelligence Engine", description = "Endpoints for Credit Scoring & Customer Risk Evaluation")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/credit-score/{customerId}")
    @Operation(summary = "Calculate customer credit score (300-850)")
    public ResponseEntity<ApiResponse<Integer>> getCreditScore(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getCustomerCreditScore(customerId)));
    }

    @GetMapping("/risk-profile/{customerId}")
    @Operation(summary = "Evaluate customer risk classification profile")
    public ResponseEntity<ApiResponse<String>> getRiskProfile(@PathVariable Long customerId, @RequestParam(required = false) BigDecimal monthlyIncome) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getCustomerRiskProfile(customerId, monthlyIncome)));
    }
}
