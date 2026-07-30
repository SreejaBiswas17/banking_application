package com.bank.enterprise.controller;

import com.bank.enterprise.common.LoanStatus;
import com.bank.enterprise.common.LoanType;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.service.LoanService;
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

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoanControllerTestFull {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LoanService loanService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /loans/apply - Should submit loan application")
    @WithMockUser
    void applyForLoan_Success() throws Exception {
        LoanDto.LoanApplicationRequest req = LoanDto.LoanApplicationRequest.builder()
                .customerId(1L)
                .loanType(LoanType.PERSONAL)
                .principalAmount(new BigDecimal("5000.00"))
                .tenureMonths(12)
                .build();

        LoanDto.LoanResponse res = LoanDto.LoanResponse.builder()
                .loanId(10L)
                .loanNumber("LN-99887766")
                .principalAmount(new BigDecimal("5000.00"))
                .status(LoanStatus.SUBMITTED)
                .build();

        when(loanService.applyForLoan(any())).thenReturn(res);

        mockMvc.perform(post("/loans/apply")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.loanNumber").value("LN-99887766"));
    }

    @Test
    @DisplayName("POST /loans/{id}/approve - Should approve loan")
    @WithMockUser(roles = "LOAN_OFFICER")
    void approveLoan_Success() throws Exception {
        LoanDto.LoanResponse res = LoanDto.LoanResponse.builder()
                .loanId(10L)
                .loanNumber("LN-99887766")
                .status(LoanStatus.APPROVED)
                .build();

        when(loanService.approveLoan(10L)).thenReturn(res);

        mockMvc.perform(post("/loans/10/approve").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("APPROVED"));
    }
}
