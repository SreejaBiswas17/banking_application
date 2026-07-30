package com.bank.enterprise.dto;

import com.bank.enterprise.common.KycStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CustomerDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCreateRequest {
        @NotNull(message = "User ID is required")
        private Long userId;

        @NotBlank(message = "First name is required")
        private String firstName;

        @NotBlank(message = "Last name is required")
        private String lastName;

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        private LocalDate dateOfBirth;

        @NotBlank(message = "Tax ID is required")
        private String taxIdNumber;

        @NotBlank(message = "National ID is required")
        private String nationalId;

        @NotBlank(message = "Address line 1 is required")
        private String addressLine1;

        private String addressLine2;

        @NotBlank(message = "City is required")
        private String city;

        @NotBlank(message = "State is required")
        private String state;

        @NotBlank(message = "Postal code is required")
        private String postalCode;

        private String country;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerResponse {
        private Long customerId;
        private Long userId;
        private String username;
        private String email;
        private String firstName;
        private String lastName;
        private LocalDate dateOfBirth;
        private String taxIdNumberMasked;
        private String nationalIdMasked;
        private KycStatus kycStatus;
        private String addressLine1;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KycUpdateDto {
        @NotNull(message = "KYC Status is required")
        private KycStatus status;
        private String remarks;
    }
}
