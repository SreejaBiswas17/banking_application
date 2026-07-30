package com.bank.enterprise.service;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.service.impl.AccountServiceImpl;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private AccountServiceImpl accountService;

    private CustomerEntity verifiedCustomer;
    private CustomerEntity unverifiedCustomer;
    private AccountEntity account;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("alice").build();
        verifiedCustomer = CustomerEntity.builder().customerId(10L).user(user).firstName("Alice").lastName("Smith").kycStatus(KycStatus.VERIFIED).build();
        unverifiedCustomer = CustomerEntity.builder().customerId(20L).user(user).kycStatus(KycStatus.PENDING).build();

        account = AccountEntity.builder()
                .accountId(100L)
                .accountNumber("1000200030")
                .customer(verifiedCustomer)
                .accountType(AccountType.SAVINGS)
                .currency(Currency.USD)
                .balance(new BigDecimal("5000.00"))
                .availableBalance(new BigDecimal("5000.00"))
                .accountStatus(AccountStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("Should successfully create a savings account for verified customer")
    void createAccount_VerifiedCustomer_Success() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(verifiedCustomer));
        when(accountRepository.save(any(AccountEntity.class))).thenReturn(account);

        AccountDto.AccountCreateRequest req = AccountDto.AccountCreateRequest.builder()
                .customerId(10L)
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("500.00"))
                .build();

        AccountDto.AccountResponse res = accountService.createAccount(req);

        assertThat(res).isNotNull();
        assertThat(res.getAccountNumber()).isEqualTo("1000200030");
        assertThat(res.getAccountType()).isEqualTo(AccountType.SAVINGS);
    }

    @Test
    @DisplayName("Should throw exception when customer KYC is not verified")
    void createAccount_UnverifiedCustomer_ThrowsException() {
        when(customerRepository.findById(20L)).thenReturn(Optional.of(unverifiedCustomer));

        AccountDto.AccountCreateRequest req = AccountDto.AccountCreateRequest.builder()
                .customerId(20L)
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("500.00"))
                .build();

        assertThatThrownBy(() -> accountService.createAccount(req))
                .isInstanceOf(BankException.class)
                .hasMessageContaining("Cannot open account. Customer KYC status is PENDING");
    }

    @Test
    @DisplayName("Should correctly return account balance")
    void checkBalance_Success() {
        when(accountRepository.findByAccountNumber("1000200030")).thenReturn(Optional.of(account));

        AccountDto.BalanceResponse res = accountService.checkBalance("1000200030");

        assertThat(res.getAvailableBalance()).isEqualByComparingTo("5000.00");
        assertThat(res.getCurrency()).isEqualTo(Currency.USD);
    }
}
