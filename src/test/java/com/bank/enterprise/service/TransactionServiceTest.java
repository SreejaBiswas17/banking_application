package com.bank.enterprise.service;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.InsufficientBalanceException;
import com.bank.enterprise.exception.InvalidTransactionException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.TransactionEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.TransactionRepository;
import com.bank.enterprise.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuditService auditService;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private AccountEntity sourceAccount;
    private AccountEntity destAccount;

    @BeforeEach
    void setUp() {
        UserEntity user1 = UserEntity.builder().userId(1L).username("user1").build();
        CustomerEntity cust1 = CustomerEntity.builder().customerId(10L).user(user1).build();
        sourceAccount = AccountEntity.builder()
                .accountId(100L)
                .accountNumber("1111222233")
                .customer(cust1)
                .balance(new BigDecimal("1000.00"))
                .availableBalance(new BigDecimal("1000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();

        UserEntity user2 = UserEntity.builder().userId(2L).username("user2").build();
        CustomerEntity cust2 = CustomerEntity.builder().customerId(20L).user(user2).build();
        destAccount = AccountEntity.builder()
                .accountId(200L)
                .accountNumber("4444555566")
                .customer(cust2)
                .balance(new BigDecimal("500.00"))
                .availableBalance(new BigDecimal("500.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should successfully transfer funds between active accounts")
    void transferFunds_Success() {
        when(accountRepository.findByAccountNumberWithLock("1111222233")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberWithLock("4444555566")).thenReturn(Optional.of(destAccount));
        when(transactionRepository.calculateDailyTransferSum(eq(100L), any(), any())).thenReturn(BigDecimal.ZERO);

        TransactionEntity savedTx = TransactionEntity.builder()
                .transactionId(500L)
                .transactionReference("TXN-12345678")
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .transactionType(TransactionType.INTERNAL_TRANSFER)
                .amount(new BigDecimal("200.00"))
                .feeAmount(BigDecimal.ZERO)
                .status(TransactionStatus.COMPLETED)
                .build();

        when(transactionRepository.save(any(TransactionEntity.class))).thenReturn(savedTx);

        TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                .sourceAccountNumber("1111222233")
                .destinationAccountNumber("4444555566")
                .amount(new BigDecimal("200.00"))
                .transferType(TransactionType.INTERNAL_TRANSFER)
                .build();

        TransactionDto.TransactionResponse res = transactionService.transferFunds(req);

        assertThat(res).isNotNull();
        assertThat(sourceAccount.getBalance()).isEqualByComparingTo("800.00");
        assertThat(destAccount.getBalance()).isEqualByComparingTo("700.00");
    }

    @Test
    @DisplayName("Should throw InsufficientBalanceException when balance is inadequate")
    void transferFunds_InsufficientBalance_ThrowsException() {
        when(accountRepository.findByAccountNumberWithLock("1111222233")).thenReturn(Optional.of(sourceAccount));
        when(accountRepository.findByAccountNumberWithLock("4444555566")).thenReturn(Optional.of(destAccount));

        TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                .sourceAccountNumber("1111222233")
                .destinationAccountNumber("4444555566")
                .amount(new BigDecimal("5000.00"))
                .transferType(TransactionType.INTERNAL_TRANSFER)
                .build();

        assertThatThrownBy(() -> transactionService.transferFunds(req))
                .isInstanceOf(InsufficientBalanceException.class);
    }

    @Test
    @DisplayName("Should throw InvalidTransactionException when transfer to self")
    void transferFunds_SameAccount_ThrowsException() {
        TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                .sourceAccountNumber("1111222233")
                .destinationAccountNumber("1111222233")
                .amount(new BigDecimal("100.00"))
                .build();

        assertThatThrownBy(() -> transactionService.transferFunds(req))
                .isInstanceOf(InvalidTransactionException.class)
                .hasMessageContaining("Source and destination accounts cannot be identical");
    }
}
