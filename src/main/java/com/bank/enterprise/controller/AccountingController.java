package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.JournalEntryEntity;
import com.bank.enterprise.service.DoubleEntryLedgerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/accounting")
@RequiredArgsConstructor
@Tag(name = "Core Accounting & General Ledger", description = "Endpoints for Double-Entry Bookkeeping & General Ledger Journal Entries")
public class AccountingController {

    private final DoubleEntryLedgerService ledgerService;

    @PostMapping("/journals/post")
    @Operation(summary = "Post a double-entry balanced journal entry")
    public ResponseEntity<ApiResponse<JournalEntryEntity>> postJournal(
            @RequestParam String debitGlCode,
            @RequestParam String creditGlCode,
            @RequestParam BigDecimal amount,
            @RequestParam String narration) {
        return ResponseEntity.ok(ApiResponse.success(ledgerService.postJournalEntry(debitGlCode, creditGlCode, amount, narration), "Journal posted"));
    }
}
