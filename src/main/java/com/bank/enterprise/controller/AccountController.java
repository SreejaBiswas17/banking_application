package com.bank.enterprise.controller;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.PageResponse;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "Endpoints for Bank Account Lifecycle & Balance Tracking")
public class AccountController {

    private final AccountService accountService;

    @PostMapping
    @Operation(summary = "Open a new bank account")
    public ResponseEntity<ApiResponse<AccountDto.AccountResponse>> openAccount(@Valid @RequestBody AccountDto.AccountCreateRequest request) {
        AccountDto.AccountResponse response = accountService.createAccount(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Account opened successfully"), HttpStatus.CREATED);
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get account details by account number")
    public ResponseEntity<ApiResponse<AccountDto.AccountResponse>> getByAccountNumber(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getAccountByNumber(accountNumber)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all accounts for a customer")
    public ResponseEntity<ApiResponse<List<AccountDto.AccountResponse>>> getByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(accountService.getAccountsByCustomerId(customerId)));
    }

    @GetMapping("/balance/{accountNumber}")
    @Operation(summary = "Check account balance")
    public ResponseEntity<ApiResponse<AccountDto.BalanceResponse>> checkBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(accountService.checkBalance(accountNumber)));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update account status (Active, Frozen, Closed)")
    public ResponseEntity<ApiResponse<AccountDto.AccountResponse>> updateStatus(@PathVariable Long id, @RequestParam AccountStatus status) {
        return ResponseEntity.ok(ApiResponse.success(accountService.updateAccountStatus(id, status), "Account status updated"));
    }

    @GetMapping
    @Operation(summary = "Get all accounts (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<AccountDto.AccountResponse>>> getAllAccounts(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(accountService.getAllAccounts(pageable))));
    }

    @PostMapping("/apply-interest")
    @Operation(summary = "Trigger monthly interest calculation batch job")
    public ResponseEntity<ApiResponse<String>> applyInterest() {
        accountService.applyInterestToSavingsAccounts();
        return ResponseEntity.ok(ApiResponse.success("Monthly interest batch completed successfully"));
    }
}
