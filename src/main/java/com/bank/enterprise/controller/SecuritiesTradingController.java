package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;
import com.bank.enterprise.service.SecuritiesTradingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/securities")
@RequiredArgsConstructor
@Tag(name = "Securities & Stock Trading", description = "Endpoints for DEMAT Trading Accounts & Equities Market Execution")
public class SecuritiesTradingController {

    private final SecuritiesTradingService securitiesService;

    @PostMapping("/demat/open")
    @Operation(summary = "Open Demat Securities Trading Account")
    public ResponseEntity<ApiResponse<SecuritiesPortfolioEntity>> openDemat(@RequestParam Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(securitiesService.openDematPortfolio(customerId), "Demat portfolio opened"));
    }

    @PostMapping("/orders/trade")
    @Operation(summary = "Execute real-time stock buy/sell order")
    public ResponseEntity<ApiResponse<StockOrderEntity>> executeTrade(
            @RequestParam Long customerId,
            @RequestParam Long accountId,
            @RequestParam String tickerSymbol,
            @RequestParam String orderType,
            @RequestParam Integer quantity,
            @RequestParam BigDecimal price) {
        return ResponseEntity.ok(ApiResponse.success(securitiesService.executeStockTrade(customerId, accountId, tickerSymbol, orderType, quantity, price), "Stock trade executed"));
    }
}
