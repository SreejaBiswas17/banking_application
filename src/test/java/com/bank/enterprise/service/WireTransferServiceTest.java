package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.FedWirePaymentEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.FedWireRepository;
import com.bank.enterprise.service.impl.WireTransferServiceImpl;
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
class WireTransferServiceTest {

    @Mock
    private FedWireRepository fedWireRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private WireTransferServiceImpl wireService;

    private AccountEntity sender;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("wire_user").build();
        CustomerEntity customer = CustomerEntity.builder().customerId(10L).user(user).build();
        sender = AccountEntity.builder().accountId(100L).accountNumber("111222333").customer(customer).balance(new BigDecimal("50000.00")).availableBalance(new BigDecimal("50000.00")).build();
    }

    @Test
    @DisplayName("Should successfully execute FedWire transfer")
    void executeFedWireTransfer_Success() {
        when(accountRepository.findById(100L)).thenReturn(Optional.of(sender));

        FedWirePaymentEntity wire = FedWirePaymentEntity.builder()
                .wireId(1L)
                .imadNumber("20260730FEDW12345678")
                .omadNumber("20260730OMAD12345678")
                .senderAccount(sender)
                .beneficiaryRoutingNumber("021000021")
                .beneficiaryAccountNumber("987654321")
                .beneficiaryName("JPMorgan Chase")
                .amount(new BigDecimal("10000.00"))
                .currency(Currency.USD)
                .status("CLEARED")
                .build();

        when(fedWireRepository.save(any(FedWirePaymentEntity.class))).thenReturn(wire);

        FedWirePaymentEntity result = wireService.executeFedWireTransfer(100L, "021000021", "987654321", "JPMorgan Chase", new BigDecimal("10000.00"), Currency.USD);

        assertThat(result).isNotNull();
        assertThat(result.getImadNumber()).contains("FEDW");
        assertThat(result.getStatus()).isEqualTo("CLEARED");
        verify(transactionService, times(1)).transferFunds(any());
    }
}
