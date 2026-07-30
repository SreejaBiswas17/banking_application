package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.PageResponse;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Processing", description = "Endpoints for Deposits, Transfers, Statements, and Reversals")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    @Operation(summary = "Cash or Online Deposit into account")
    public ResponseEntity<ApiResponse<TransactionDto.TransactionResponse>> deposit(@Valid @RequestBody TransactionDto.DepositRequest request) {
        TransactionDto.TransactionResponse response = transactionService.deposit(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Deposit completed"), HttpStatus.CREATED);
    }

    @PostMapping("/transfer")
    @Operation(summary = "Transfer funds between accounts (Internal, NEFT, RTGS, IMPS)")
    public ResponseEntity<ApiResponse<TransactionDto.TransactionResponse>> transfer(@Valid @RequestBody TransactionDto.TransferRequest request) {
        TransactionDto.TransactionResponse response = transactionService.transferFunds(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Fund transfer completed"), HttpStatus.CREATED);
    }

    @GetMapping("/reference/{ref}")
    @Operation(summary = "Get transaction details by reference")
    public ResponseEntity<ApiResponse<TransactionDto.TransactionResponse>> getByReference(@PathVariable String ref) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionByReference(ref)));
    }

    @GetMapping("/statement/{accountNumber}")
    @Operation(summary = "Get transaction statement for account")
    public ResponseEntity<ApiResponse<PageResponse<TransactionDto.TransactionResponse>>> getStatement(
            @PathVariable String accountNumber,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(transactionService.getAccountStatement(accountNumber, startDate, endDate, pageable))));
    }

    @PostMapping("/reverse/{reference}")
    @Operation(summary = "Reverse a completed transaction (Admin/Auditor only)")
    public ResponseEntity<ApiResponse<TransactionDto.TransactionResponse>> reverseTransaction(@PathVariable String reference, @RequestParam String reason) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.reverseTransaction(reference, reason), "Transaction reversed"));
    }
}
