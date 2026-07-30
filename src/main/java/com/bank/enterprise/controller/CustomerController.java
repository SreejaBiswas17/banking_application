package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.PageResponse;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.service.CustomerService;
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
@RequestMapping("/customers")
@RequiredArgsConstructor
@Tag(name = "Customer Management", description = "Endpoints for Customer KYC and Profile Management")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create customer profile")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> createProfile(@Valid @RequestBody CustomerDto.CustomerCreateRequest request) {
        CustomerDto.CustomerResponse response = customerService.createCustomerProfile(request);
        return new ResponseEntity<>(ApiResponse.success(response, "Customer profile created"), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get customer profile by ID")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomerById(id)));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get customer profile by User ID")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> getCustomerByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(customerService.getCustomerByUserId(userId)));
    }

    @PutMapping("/{id}/kyc")
    @Operation(summary = "Update KYC status for customer")
    public ResponseEntity<ApiResponse<CustomerDto.CustomerResponse>> updateKyc(@PathVariable Long id, @Valid @RequestBody CustomerDto.KycUpdateDto dto) {
        return ResponseEntity.ok(ApiResponse.success(customerService.updateKycStatus(id, dto), "KYC updated"));
    }

    @GetMapping
    @Operation(summary = "Get all customers (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<CustomerDto.CustomerResponse>>> getAllCustomers(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(customerService.getAllCustomers(pageable))));
    }

    @GetMapping("/search")
    @Operation(summary = "Search customers by name or Tax ID")
    public ResponseEntity<ApiResponse<List<CustomerDto.CustomerResponse>>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(ApiResponse.success(customerService.searchCustomers(keyword)));
    }
}
