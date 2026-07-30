package com.bank.enterprise.controller;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.service.AccountService;
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

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /accounts - Should create account and return 201 Created")
    @WithMockUser
    void openAccount_ShouldReturn201() throws Exception {
        AccountDto.AccountCreateRequest req = AccountDto.AccountCreateRequest.builder()
                .customerId(1L)
                .accountType(AccountType.SAVINGS)
                .currency(Currency.USD)
                .initialDeposit(new BigDecimal("500.00"))
                .build();

        AccountDto.AccountResponse res = AccountDto.AccountResponse.builder()
                .accountId(10L)
                .accountNumber("9988776655")
                .customerId(1L)
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("500.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        when(accountService.createAccount(any())).thenReturn(res);

        mockMvc.perform(post("/accounts")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accountNumber").value("9988776655"));
    }

    @Test
    @DisplayName("GET /accounts/balance/{accountNumber} - Should return balance details")
    @WithMockUser
    void checkBalance_ShouldReturn200() throws Exception {
        AccountDto.BalanceResponse res = AccountDto.BalanceResponse.builder()
                .accountNumber("9988776655")
                .ledgerBalance(new BigDecimal("1500.00"))
                .availableBalance(new BigDecimal("1500.00"))
                .currency(Currency.USD)
                .build();

        when(accountService.checkBalance("9988776655")).thenReturn(res);

        mockMvc.perform(get("/accounts/balance/9988776655"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.availableBalance").value(1500.00));
    }
}
