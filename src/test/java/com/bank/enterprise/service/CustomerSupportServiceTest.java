package com.bank.enterprise.service;

import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.SupportTicketEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.SupportTicketRepository;
import com.bank.enterprise.repository.TicketMessageRepository;
import com.bank.enterprise.service.impl.CustomerSupportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerSupportServiceTest {

    @Mock
    private SupportTicketRepository ticketRepository;

    @Mock
    private TicketMessageRepository messageRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private CustomerSupportServiceImpl supportService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("testuser").build();
        customer = CustomerEntity.builder().customerId(10L).user(user).firstName("Jane").lastName("Doe").build();
    }

    @Test
    @DisplayName("Should successfully create a support ticket with initial message")
    void createTicket_Success() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        SupportTicketEntity ticket = SupportTicketEntity.builder()
                .ticketId(100L)
                .ticketNumber("TKT-12345678")
                .customer(customer)
                .subject("Wrong Debit Charge")
                .category("TRANSACTION_DISPUTE")
                .priority("HIGH")
                .status("OPEN")
                .build();

        when(ticketRepository.save(any(SupportTicketEntity.class))).thenReturn(ticket);

        SupportTicketEntity result = supportService.createTicket(10L, "Wrong Debit Charge", "TRANSACTION_DISPUTE", "HIGH", "I was charged twice");

        assertThat(result).isNotNull();
        assertThat(result.getTicketNumber()).isEqualTo("TKT-12345678");
        verify(messageRepository, times(1)).save(any());
    }
}
