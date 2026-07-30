package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.FedWirePaymentEntity;
import com.bank.enterprise.service.WireTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/wire-transfers")
@RequiredArgsConstructor
@Tag(name = "FedWire & Interbank Clearance", description = "Endpoints for Federal Reserve Wire Transfers & RTGS Clearance")
public class WireTransferController {

    private final WireTransferService wireService;

    @PostMapping("/fedwire/send")
    @Operation(summary = "Transmit Outward FedWire RTGS Payment")
    public ResponseEntity<ApiResponse<FedWirePaymentEntity>> sendFedWire(
            @RequestParam Long senderAccountId,
            @RequestParam String routingNumber,
            @RequestParam String accountNumber,
            @RequestParam String beneficiaryName,
            @RequestParam BigDecimal amount,
            @RequestParam Currency currency) {
        return ResponseEntity.ok(ApiResponse.success(wireService.executeFedWireTransfer(senderAccountId, routingNumber, accountNumber, beneficiaryName, amount, currency), "FedWire transmitted"));
    }
}
