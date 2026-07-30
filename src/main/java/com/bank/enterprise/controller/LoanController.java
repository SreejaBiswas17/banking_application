package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.PageResponse;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.service.LoanService;
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
@RequestMapping("/loans")
@RequiredArgsConstructor
@Tag(name = "Loan & Credit Operations", description = "Endpoints for Loan Applications, EMI Processing & Disbursal")
public class LoanController {

    private final LoanService loanService;

    @PostMapping("/apply")
    @Operation(summary = "Apply for a loan facility")
    public ResponseEntity<ApiResponse<LoanDto.LoanResponse>> applyForLoan(@Valid @RequestBody LoanDto.LoanApplicationRequest request) {
        LoanDto.LoanResponse response = loanService.applyForLoan(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Loan application submitted"), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/approve")
    @Operation(summary = "Approve loan application (Loan Officer only)")
    public ResponseEntity<ApiResponse<LoanDto.LoanResponse>> approveLoan(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(loanService.approveLoan(id), "Loan approved"));
    }

    @PostMapping("/{id}/disburse")
    @Operation(summary = "Disburse approved loan amount into customer account")
    public ResponseEntity<ApiResponse<LoanDto.LoanResponse>> disburseLoan(@PathVariable Long id, @RequestParam String destinationAccountNumber) {
        return ResponseEntity.ok(ApiResponse.success(loanService.disburseLoan(id, destinationAccountNumber), "Loan disbursed"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan details with EMI schedule")
    public ResponseEntity<ApiResponse<LoanDto.LoanResponse>> getLoanById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoanById(id)));
    }

    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all loans for customer")
    public ResponseEntity<ApiResponse<List<LoanDto.LoanResponse>>> getLoansByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(loanService.getLoansByCustomerId(customerId)));
    }

    @PostMapping("/emi/{scheduleId}/pay")
    @Operation(summary = "Pay EMI installment")
    public ResponseEntity<ApiResponse<String>> payEmi(@PathVariable Long scheduleId, @RequestParam String sourceAccountNumber) {
        loanService.payLoanEmi(scheduleId, sourceAccountNumber);
        return ResponseEntity.ok(ApiResponse.success("EMI payment processed successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all loans (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<LoanDto.LoanResponse>>> getAllLoans(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(loanService.getAllLoans(pageable))));
    }
}
