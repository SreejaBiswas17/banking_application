package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.ForexContractEntity;
import com.bank.enterprise.service.ForexService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/forex")
@RequiredArgsConstructor
@Tag(name = "Foreign Exchange Operations", description = "Endpoints for FX Spot Contracts & Currency Conversions")
public class ForexController {

    private final ForexService forexService;

    @PostMapping("/contracts/book")
    @Operation(summary = "Book a foreign exchange spot contract")
    public ResponseEntity<ApiResponse<ForexContractEntity>> bookContract(
            @RequestParam Long customerId,
            @RequestParam Currency buyCurrency,
            @RequestParam Currency sellCurrency,
            @RequestParam BigDecimal buyAmount) {
        return ResponseEntity.ok(ApiResponse.success(forexService.bookContract(customerId, buyCurrency, sellCurrency, buyAmount), "Forex contract booked"));
    }

    @GetMapping("/contracts/{contractNumber}")
    @Operation(summary = "Get Forex contract details")
    public ResponseEntity<ApiResponse<ForexContractEntity>> getContract(@PathVariable String contractNumber) {
        return ResponseEntity.ok(ApiResponse.success(forexService.getContractByNumber(contractNumber)));
    }

    @GetMapping("/contracts/customer/{customerId}")
    @Operation(summary = "Get all Forex contracts for customer")
    public ResponseEntity<ApiResponse<List<ForexContractEntity>>> getCustomerContracts(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(forexService.getCustomerContracts(customerId)));
    }
}
