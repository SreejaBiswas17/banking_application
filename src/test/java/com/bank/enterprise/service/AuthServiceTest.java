package com.bank.enterprise.service;

import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.config.JwtTokenProvider;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.exception.AccountLockedException;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.UserRepository;
import com.bank.enterprise.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserEntity sampleUser;
    private UserDto.RegisterRequest registerRequest;
    private UserDto.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        sampleUser = UserEntity.builder()
                .userId(1L)
                .username("john_doe")
                .passwordHash("encoded_password")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .userRole(UserRole.CUSTOMER)
                .isEnabled(true)
                .isLocked(false)
                .failedAttempts(0)
                .build();

        registerRequest = UserDto.RegisterRequest.builder()
                .username("john_doe")
                .password("Secret123#")
                .email("john@example.com")
                .phoneNumber("1234567890")
                .role(UserRole.CUSTOMER)
                .build();

        loginRequest = UserDto.LoginRequest.builder()
                .username("john_doe")
                .password("Secret123#")
                .build();
    }

    @Nested
    @DisplayName("Registration Tests")
    class RegistrationTests {

        @Test
        @DisplayName("Should successfully register a new user")
        void registerUser_Success() {
            when(userRepository.existsByUsername("john_doe")).thenReturn(false);
            when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
            when(passwordEncoder.encode("Secret123#")).thenReturn("encoded_password");
            when(userRepository.save(any(UserEntity.class))).thenReturn(sampleUser);

            UserDto.AuthResponse response = authService.registerUser(registerRequest);

            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo("john_doe");
            assertThat(response.getRole()).isEqualTo(UserRole.CUSTOMER);
            verify(userRepository, times(1)).save(any(UserEntity.class));
            verify(auditService, times(1)).logAction(eq("USER_REGISTRATION"), eq("john_doe"), eq("USER"), anyString(), any(), anyString());
        }

        @Test
        @DisplayName("Should throw exception when username already exists")
        void registerUser_DuplicateUsername_ThrowsException() {
            when(userRepository.existsByUsername("john_doe")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(registerRequest))
                    .isInstanceOf(BankException.class)
                    .hasMessageContaining("Username 'john_doe' is already taken");

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw exception when email already exists")
        void registerUser_DuplicateEmail_ThrowsException() {
            when(userRepository.existsByUsername("john_doe")).thenReturn(false);
            when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authService.registerUser(registerRequest))
                    .isInstanceOf(BankException.class)
                    .hasMessageContaining("Email 'john@example.com' is already registered");

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {

        @Test
        @DisplayName("Should successfully authenticate user and return JWT token")
        void authenticateUser_Success() {
            Authentication authentication = mock(Authentication.class);
            CustomerEntity customer = CustomerEntity.builder().customerId(10L).build();

            when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(sampleUser));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
            when(tokenProvider.generateToken(authentication)).thenReturn("sample_jwt_token");
            when(customerRepository.findByUser_UserId(1L)).thenReturn(Optional.of(customer));

            UserDto.AuthResponse response = authService.authenticateUser(loginRequest);

            assertThat(response).isNotNull();
            assertThat(response.getToken()).isEqualTo("sample_jwt_token");
            assertThat(response.getCustomerId()).isEqualTo(10L);
            verify(userRepository, times(1)).resetFailedAttempts("john_doe");
        }

        @Test
        @DisplayName("Should throw AccountLockedException when account is locked")
        void authenticateUser_LockedAccount_ThrowsException() {
            sampleUser.setIsLocked(true);
            when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(sampleUser));

            assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                    .isInstanceOf(AccountLockedException.class)
                    .hasMessageContaining("Account is locked");
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when user does not exist")
        void authenticateUser_UserNotFound_ThrowsException() {
            when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
            loginRequest.setUsername("unknown");

            assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
