package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.SafeDepositLockerEntity;
import com.bank.enterprise.service.SafeDepositVaultService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vault")
@RequiredArgsConstructor
@Tag(name = "Safe Deposit Locker Vault", description = "Endpoints for Safe Deposit Lockers & Physical Vault Access")
public class SafeDepositVaultController {

    private final SafeDepositVaultService vaultService;

    @PostMapping("/lockers/rent")
    @Operation(summary = "Rent a safe deposit vault locker")
    public ResponseEntity<ApiResponse<SafeDepositLockerEntity>> rentLocker(@RequestParam Long customerId, @RequestParam String lockerSize) {
        return ResponseEntity.ok(ApiResponse.success(vaultService.rentLocker(customerId, lockerSize), "Locker rented"));
    }

    @GetMapping("/lockers/vacant")
    @Operation(summary = "Get list of vacant available lockers")
    public ResponseEntity<ApiResponse<List<SafeDepositLockerEntity>>> getVacantLockers() {
        return ResponseEntity.ok(ApiResponse.success(vaultService.getAvailableLockers()));
    }
}
