package com.bank.enterprise.integration;

import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserLifecycleIntegrationTest {

    @Autowired
    private AuthService authService;

    @Test
    @DisplayName("Complete Integration: Register -> Authenticate -> Change Password")
    void fullUserLifecycle() {
        UserDto.RegisterRequest regReq = UserDto.RegisterRequest.builder()
                .username("integration_user_1")
                .password("Password123#")
                .email("int1@example.com")
                .phoneNumber("1234567890")
                .role(UserRole.CUSTOMER)
                .build();

        UserDto.AuthResponse regRes = authService.registerUser(regReq);
        assertThat(regRes).isNotNull();
        assertThat(regRes.getUsername()).isEqualTo("integration_user_1");

        UserDto.LoginRequest loginReq = UserDto.LoginRequest.builder()
                .username("integration_user_1")
                .password("Password123#")
                .build();

        UserDto.AuthResponse loginRes = authService.authenticateUser(loginReq);
        assertThat(loginRes.getToken()).isNotNull();

        UserDto.PasswordChangeRequest passReq = UserDto.PasswordChangeRequest.builder()
                .oldPassword("Password123#")
                .newPassword("NewPassword456#")
                .build();

        authService.changePassword("integration_user_1", passReq);

        // Verify authentication with new password
        UserDto.LoginRequest newLoginReq = UserDto.LoginRequest.builder()
                .username("integration_user_1")
                .password("NewPassword456#")
                .build();

        UserDto.AuthResponse newLoginRes = authService.authenticateUser(newLoginReq);
        assertThat(newLoginRes.getToken()).isNotNull();
    }
}
