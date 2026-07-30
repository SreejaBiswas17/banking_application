package com.bank.enterprise.controller;

import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.service.CustomerService;
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

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@AutoConfigureMockMvc(addFilters = false)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /customers - Should create customer profile successfully")
    @WithMockUser
    void createProfile_Success() throws Exception {
        CustomerDto.CustomerCreateRequest req = CustomerDto.CustomerCreateRequest.builder()
                .userId(1L)
                .firstName("John")
                .lastName("Doe")
                .dateOfBirth(LocalDate.of(1990, 1, 1))
                .taxIdNumber("TAX123")
                .nationalId("NAT123")
                .addressLine1("Street 1")
                .city("City")
                .state("State")
                .postalCode("12345")
                .build();

        CustomerDto.CustomerResponse res = CustomerDto.CustomerResponse.builder()
                .customerId(10L)
                .firstName("John")
                .lastName("Doe")
                .kycStatus(KycStatus.PENDING)
                .build();

        when(customerService.createCustomerProfile(any())).thenReturn(res);

        mockMvc.perform(post("/customers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    @Test
    @DisplayName("GET /customers/{id} - Should return customer details")
    @WithMockUser
    void getCustomerById_Success() throws Exception {
        CustomerDto.CustomerResponse res = CustomerDto.CustomerResponse.builder()
                .customerId(10L)
                .firstName("John")
                .lastName("Doe")
                .build();

        when(customerService.getCustomerById(10L)).thenReturn(res);

        mockMvc.perform(get("/customers/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.customerId").value(10));
    }
}
