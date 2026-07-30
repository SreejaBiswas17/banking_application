package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.EscrowAgreementEntity;
import com.bank.enterprise.service.EscrowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/escrow")
@RequiredArgsConstructor
@Tag(name = "Escrow & Third-Party Custody", description = "Endpoints for Buyer-Seller Escrow Vault Custody & Milestone Releases")
public class EscrowController {

    private final EscrowService escrowService;

    @PostMapping("/agreements/setup")
    @Operation(summary = "Establish new escrow custody agreement")
    public ResponseEntity<ApiResponse<EscrowAgreementEntity>> setupEscrow(
            @RequestParam Long buyerCustomerId,
            @RequestParam Long sellerCustomerId,
            @RequestParam Long escrowAccountId,
            @RequestParam BigDecimal totalAmount,
            @RequestParam Currency currency) {
        return ResponseEntity.ok(ApiResponse.success(escrowService.setupEscrowAgreement(buyerCustomerId, sellerCustomerId, escrowAccountId, totalAmount, currency), "Escrow setup completed"));
    }

    @PostMapping("/agreements/{escrowId}/release")
    @Operation(summary = "Release escrow milestone funds to seller account")
    public ResponseEntity<ApiResponse<String>> releaseFunds(
            @PathVariable Long escrowId,
            @RequestParam BigDecimal releaseAmount,
            @RequestParam String sellerAccountNumber) {
        escrowService.releaseEscrowFunds(escrowId, releaseAmount, sellerAccountNumber);
        return ResponseEntity.ok(ApiResponse.success("Escrow milestone released successfully"));
    }
}
