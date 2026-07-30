package com.bank.enterprise.integration;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AccountAndDepositIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Complete Integration: Register -> Create Profile -> Verify KYC -> Open Account -> Deposit Cash")
    void fullAccountDepositFlow() {
        // Step 1: Register User
        UserDto.AuthResponse userRes = authService.registerUser(UserDto.RegisterRequest.builder()
                .username("acc_flow_user")
                .password("Pass123#")
                .email("acc_flow@test.com")
                .phoneNumber("9988776655")
                .role(UserRole.CUSTOMER)
                .build());

        // Step 2: Create Customer Profile
        CustomerDto.CustomerResponse custRes = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder()
                .userId(userRes.getUserId())
                .firstName("Michael")
                .lastName("Jordan")
                .dateOfBirth(LocalDate.of(1988, 6, 23))
                .taxIdNumber("TAX-MJ23")
                .nationalId("NAT-MJ23")
                .addressLine1("23 Chicago Bull Way")
                .city("Chicago")
                .state("IL")
                .postalCode("60601")
                .build());

        // Step 3: Verify KYC
        customerService.updateKycStatus(custRes.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        // Step 4: Open Savings Account
        AccountDto.AccountResponse accRes = accountService.createAccount(AccountDto.AccountCreateRequest.builder()
                .customerId(custRes.getCustomerId())
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("1000.00"))
                .build());

        assertThat(accRes.getBalance()).isEqualByComparingTo("1000.00");

        // Step 5: Deposit Cash
        TransactionDto.TransactionResponse depRes = transactionService.deposit(TransactionDto.DepositRequest.builder()
                .accountNumber(accRes.getAccountNumber())
                .amount(new BigDecimal("500.00"))
                .description("Over-the-counter deposit")
                .build());

        assertThat(depRes.getStatus()).isEqualTo(com.bank.enterprise.common.TransactionStatus.COMPLETED);

        // Step 6: Verify final balance
        AccountDto.BalanceResponse balRes = accountService.checkBalance(accRes.getAccountNumber());
        assertThat(balRes.getAvailableBalance()).isEqualByComparingTo("1500.00");
    }
}
