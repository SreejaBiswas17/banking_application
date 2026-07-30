package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.ForexContractEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.ForexContractRepository;
import com.bank.enterprise.service.impl.ForexServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ForexServiceTest {

    @Mock
    private ForexContractRepository forexRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ForexServiceImpl forexService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder().customerId(1L).build();
    }

    @Test
    @DisplayName("Should successfully book Forex spot contract")
    void bookContract_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        ForexContractEntity contract = ForexContractEntity.builder()
                .contractId(10L)
                .contractNumber("FX-12345678")
                .customer(customer)
                .buyCurrency(Currency.EUR)
                .sellCurrency(Currency.USD)
                .buyAmount(new BigDecimal("10000.00"))
                .sellAmount(new BigDecimal("10850.00"))
                .appliedRate(new BigDecimal("1.0850"))
                .status("BOOKED")
                .build();

        when(forexRepository.save(any(ForexContractEntity.class))).thenReturn(contract);

        ForexContractEntity result = forexService.bookContract(1L, Currency.EUR, Currency.USD, new BigDecimal("10000.00"));

        assertThat(result).isNotNull();
        assertThat(result.getContractNumber()).isEqualTo("FX-12345678");
        assertThat(result.getStatus()).isEqualTo("BOOKED");
    }
}
