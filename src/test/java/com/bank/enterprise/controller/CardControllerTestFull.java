package com.bank.enterprise.controller;

import com.bank.enterprise.common.CardStatus;
import com.bank.enterprise.common.CardType;
import com.bank.enterprise.dto.CardDto;
import com.bank.enterprise.service.CardService;
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
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTestFull {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CardService cardService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /cards/issue - Should issue new card and return 201 Created")
    @WithMockUser
    void issueCard_Success() throws Exception {
        CardDto.CardIssueRequest req = CardDto.CardIssueRequest.builder()
                .accountId(1L)
                .cardType(CardType.DEBIT_PLATINUM)
                .pin("4321")
                .build();

        CardDto.CardResponse res = CardDto.CardResponse.builder()
                .cardId(10L)
                .cardNumberMasked("4532-XXXX-XXXX-9999")
                .cardType(CardType.DEBIT_PLATINUM)
                .cardStatus(CardStatus.ACTIVE)
                .build();

        when(cardService.issueCard(any())).thenReturn(res);

        mockMvc.perform(post("/cards/issue")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.cardNumberMasked").value("4532-XXXX-XXXX-9999"));
    }
}
