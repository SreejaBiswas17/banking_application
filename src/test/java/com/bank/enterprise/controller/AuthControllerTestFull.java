package com.bank.enterprise.controller;

import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTestFull {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /auth/register - Success 201 Created")
    void registerUser_Success() throws Exception {
        UserDto.RegisterRequest req = UserDto.RegisterRequest.builder()
                .username("test_register")
                .password("Password123#")
                .email("testreg@example.com")
                .phoneNumber("1234567890")
                .role(UserRole.CUSTOMER)
                .build();

        UserDto.AuthResponse res = UserDto.AuthResponse.builder()
                .token("TOKEN_123")
                .username("test_register")
                .role(UserRole.CUSTOMER)
                .build();

        when(authService.registerUser(any())).thenReturn(res);

        mockMvc.perform(post("/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.username").value("test_register"));
    }

    @Test
    @DisplayName("POST /auth/login - Success 200 OK")
    void loginUser_Success() throws Exception {
        UserDto.LoginRequest req = UserDto.LoginRequest.builder()
                .username("test_user")
                .password("Password123#")
                .build();

        UserDto.AuthResponse res = UserDto.AuthResponse.builder()
                .token("JWT_TOKEN_ABC")
                .username("test_user")
                .build();

        when(authService.authenticateUser(any())).thenReturn(res);

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("JWT_TOKEN_ABC"));
    }

    @Test
    @DisplayName("POST /auth/unlock/{username} - Success 200 OK")
    @WithMockUser(roles = "ADMIN")
    void unlockUser_Success() throws Exception {
        doNothing().when(authService).unlockUser("locked_user");

        mockMvc.perform(post("/auth/unlock/locked_user").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
