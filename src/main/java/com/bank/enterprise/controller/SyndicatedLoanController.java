package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.ParticipantBankEntity;
import com.bank.enterprise.model.SyndicatedLoanFacilityEntity;
import com.bank.enterprise.service.SyndicatedLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/syndication")
@RequiredArgsConstructor
@Tag(name = "Syndicated Corporate Lending", description = "Endpoints for Multi-Bank Loan Syndication & Participant Share Commitments")
public class SyndicatedLoanController {

    private final SyndicatedLoanService syndicationService;

    @PostMapping("/facilities/create")
    @Operation(summary = "Create multi-million dollar syndicated loan facility")
    public ResponseEntity<ApiResponse<SyndicatedLoanFacilityEntity>> createFacility(
            @RequestParam Long borrowerCorporateId,
            @RequestParam BigDecimal totalFacilityAmount,
            @RequestParam String leadArrangerBank,
            @RequestParam Currency currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate maturityDate) {
        return ResponseEntity.ok(ApiResponse.success(syndicationService.createFacility(borrowerCorporateId, totalFacilityAmount, leadArrangerBank, currency, maturityDate), "Syndicated facility created"));
    }

    @PostMapping("/facilities/{facilityId}/participants/add")
    @Operation(summary = "Add participating bank to syndicated facility")
    public ResponseEntity<ApiResponse<ParticipantBankEntity>> addParticipant(
            @PathVariable Long facilityId,
            @RequestParam String bankName,
            @RequestParam String swiftBic,
            @RequestParam BigDecimal committedAmount) {
        return ResponseEntity.ok(ApiResponse.success(syndicationService.addParticipantBank(facilityId, bankName, swiftBic, committedAmount), "Participant bank committed"));
    }
}
