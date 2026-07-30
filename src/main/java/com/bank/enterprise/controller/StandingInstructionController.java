package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.StandingInstructionEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.StandingInstructionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/standing-instructions")
@RequiredArgsConstructor
@Tag(name = "Standing Instructions", description = "Endpoints for Automated Recurring Transfer Schedules")
public class StandingInstructionController {

    private final StandingInstructionRepository standingInstructionRepository;
    private final AccountRepository accountRepository;

    @PostMapping
    @Operation(summary = "Create recurring standing instruction")
    public ResponseEntity<ApiResponse<StandingInstructionEntity>> createInstruction(
            @RequestParam String sourceAccountNumber,
            @RequestParam String destinationAccountNumber,
            @RequestParam BigDecimal amount,
            @RequestParam Integer dayOfMonth) {

        var source = accountRepository.findByAccountNumber(sourceAccountNumber).orElseThrow();
        var dest = accountRepository.findByAccountNumber(destinationAccountNumber).orElseThrow();

        StandingInstructionEntity entity = StandingInstructionEntity.builder()
                .sourceAccount(source)
                .destinationAccount(dest)
                .amount(amount)
                .transactionType(com.bank.enterprise.common.TransactionType.INTERNAL_TRANSFER)
                .executionDayOfMonth(dayOfMonth)
                .startDate(LocalDate.now())
                .isActive(true)
                .build();

        return ResponseEntity.ok(ApiResponse.success(standingInstructionRepository.save(entity), "Standing instruction created"));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get standing instructions for account")
    public ResponseEntity<ApiResponse<List<StandingInstructionEntity>>> getByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(ApiResponse.success(standingInstructionRepository.findBySourceAccount_AccountId(accountId)));
    }
}
