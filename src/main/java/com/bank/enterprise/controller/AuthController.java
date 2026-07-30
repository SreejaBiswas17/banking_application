package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "Endpoints for user registration, login, and security operations")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user account")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> register(@Valid @RequestBody UserDto.RegisterRequest request) {
        UserDto.AuthResponse response = authService.registerUser(request);
        return new ResponseEntity<>(ApiResponse.success(response, "User registered successfully"), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and issue JWT token")
    public ResponseEntity<ApiResponse<UserDto.AuthResponse>> login(@Valid @RequestBody UserDto.LoginRequest request) {
        UserDto.AuthResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Authentication successful"));
    }

    @PostMapping("/password-change")
    @Operation(summary = "Change current user password")
    public ResponseEntity<ApiResponse<String>> changePassword(@RequestParam String username, @Valid @RequestBody UserDto.PasswordChangeRequest request) {
        authService.changePassword(username, request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/unlock/{username}")
    @Operation(summary = "Unlock a locked user account (Admin only)")
    public ResponseEntity<ApiResponse<String>> unlockUser(@PathVariable String username) {
        authService.unlockUser(username);
        return ResponseEntity.ok(ApiResponse.success("User unlocked successfully"));
    }
}
