package com.bank.enterprise.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DtoValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("UserDto.RegisterRequest - Should fail when email is invalid")
    void registerRequest_InvalidEmail() {
        UserDto.RegisterRequest req = UserDto.RegisterRequest.builder()
                .username("validuser")
                .password("validpass123")
                .email("not-an-email")
                .phoneNumber("12345678")
                .build();

        Set<ConstraintViolation<UserDto.RegisterRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Invalid email format");
    }

    @Test
    @DisplayName("TransactionDto.TransferRequest - Should fail when amount is less than 1.00")
    void transferRequest_InvalidAmount() {
        TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                .sourceAccountNumber("1111111111")
                .destinationAccountNumber("2222222222")
                .amount(new BigDecimal("0.50"))
                .build();

        Set<ConstraintViolation<TransactionDto.TransferRequest>> violations = validator.validate(req);
        assertThat(violations).hasSize(1);
        assertThat(violations.iterator().next().getMessage()).isEqualTo("Minimum transfer amount is 1.00");
    }
}
