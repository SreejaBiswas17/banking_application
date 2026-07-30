package com.bank.enterprise.service;

import com.bank.enterprise.dto.UserDto;

public interface AuthService {
    UserDto.AuthResponse registerUser(UserDto.RegisterRequest request);
    UserDto.AuthResponse authenticateUser(UserDto.LoginRequest request);
    void changePassword(String username, UserDto.PasswordChangeRequest request);
    void unlockUser(String username);
}
