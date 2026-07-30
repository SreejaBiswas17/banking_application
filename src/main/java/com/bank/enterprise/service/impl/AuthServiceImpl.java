package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Constants;
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
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuditService auditService;

    @Override
    @Transactional
    public UserDto.AuthResponse registerUser(UserDto.RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BankException("Username '" + request.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BankException("Email '" + request.getEmail() + "' is already registered");
        }

        UserRole role = request.getRole() != null ? request.getRole() : UserRole.CUSTOMER;

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .userRole(role)
                .isEnabled(true)
                .isLocked(false)
                .failedAttempts(0)
                .build();

        UserEntity savedUser = userRepository.save(user);

        auditService.logAction("USER_REGISTRATION", savedUser.getUsername(), "USER", savedUser.getUserId().toString(), null, "User Created with Role " + role);

        String token = "TOKEN_NEW_REGISTRATION_" + savedUser.getUserId();

        return UserDto.AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getUserRole())
                .userId(savedUser.getUserId())
                .build();
    }

    @Override
    @Transactional
    public UserDto.AuthResponse authenticateUser(UserDto.LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", request.getUsername()));

        if (Boolean.TRUE.equals(user.getIsLocked())) {
            throw new AccountLockedException(user.getUsername());
        }

        if (!user.getIsEnabled()) {
            throw new BankException("Account is disabled. Please contact support.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            userRepository.resetFailedAttempts(user.getUsername());
            String token = tokenProvider.generateToken(authentication);

            Optional<CustomerEntity> customerOpt = customerRepository.findByUser_UserId(user.getUserId());
            Long customerId = customerOpt.map(CustomerEntity::getCustomerId).orElse(null);

            auditService.logAction("USER_LOGIN_SUCCESS", user.getUsername(), "USER", user.getUserId().toString(), null, "Authentication Successful");

            return UserDto.AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getUserRole())
                    .userId(user.getUserId())
                    .customerId(customerId)
                    .build();

        } catch (Exception e) {
            userRepository.incrementFailedAttempts(user.getUsername());
            if (user.getFailedAttempts() + 1 >= Constants.MAX_LOGIN_ATTEMPTS) {
                userRepository.updateLockStatus(user.getUsername(), true);
                auditService.logAction("USER_ACCOUNT_LOCKED", user.getUsername(), "USER", user.getUserId().toString(), null, "Locked due to failed login attempts");
            }
            throw new BankException("Invalid username or password", e);
        }
    }

    @Override
    @Transactional
    public void changePassword(String username, UserDto.PasswordChangeRequest request) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new BankException("Incorrect current password provided");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        auditService.logAction("PASSWORD_CHANGE", username, "USER", user.getUserId().toString(), null, "Password Changed Successfully");
    }

    @Override
    @Transactional
    public void unlockUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        user.setIsLocked(false);
        user.setFailedAttempts(0);
        userRepository.save(user);

        auditService.logAction("USER_UNLOCKED", "ADMIN", "USER", user.getUserId().toString(), null, "User unlocked by Admin");
    }
}
