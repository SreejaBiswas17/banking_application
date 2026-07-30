package com.bank.enterprise.controller;

import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.service.TransactionService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService transactionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /transactions/deposit - Should deposit cash and return 201 Created")
    @WithMockUser
    void deposit_Success() throws Exception {
        TransactionDto.DepositRequest req = TransactionDto.DepositRequest.builder()
                .accountNumber("1000200030")
                .amount(new BigDecimal("250.00"))
                .build();

        TransactionDto.TransactionResponse res = TransactionDto.TransactionResponse.builder()
                .transactionId(1L)
                .transactionReference("DEP-12345678")
                .amount(new BigDecimal("250.00"))
                .status(TransactionStatus.COMPLETED)
                .build();

        when(transactionService.deposit(any())).thenReturn(res);

        mockMvc.perform(post("/transactions/deposit")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.transactionReference").value("DEP-12345678"));
    }

    @Test
    @DisplayName("POST /transactions/transfer - Should transfer funds and return 201 Created")
    @WithMockUser
    void transfer_Success() throws Exception {
        TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                .sourceAccountNumber("1111111111")
                .destinationAccountNumber("2222222222")
                .amount(new BigDecimal("100.00"))
                .transferType(TransactionType.INTERNAL_TRANSFER)
                .build();

        TransactionDto.TransactionResponse res = TransactionDto.TransactionResponse.builder()
                .transactionId(2L)
                .transactionReference("TXN-87654321")
                .amount(new BigDecimal("100.00"))
                .status(TransactionStatus.COMPLETED)
                .build();

        when(transactionService.transferFunds(any())).thenReturn(res);

        mockMvc.perform(post("/transactions/transfer")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.transactionReference").value("TXN-87654321"));
    }
}
