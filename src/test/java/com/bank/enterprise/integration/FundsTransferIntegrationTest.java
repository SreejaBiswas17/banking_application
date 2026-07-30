package com.bank.enterprise.integration;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.TransactionType;
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
class FundsTransferIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Complete Integration: Setup 2 Accounts -> Transfer Funds -> Verify Balances")
    void fullTransferFlow() {
        // Setup Customer 1 & Account 1
        UserDto.AuthResponse u1 = authService.registerUser(UserDto.RegisterRequest.builder().username("sender").password("Pass123#").email("sender@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c1 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u1.getUserId()).firstName("Sender").lastName("User").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX1").nationalId("NAT1").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c1.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse accSender = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c1.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("5000.00")).build());

        // Setup Customer 2 & Account 2
        UserDto.AuthResponse u2 = authService.registerUser(UserDto.RegisterRequest.builder().username("receiver").password("Pass123#").email("receiver@t.com").phoneNumber("222").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c2 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u2.getUserId()).firstName("Receiver").lastName("User").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX2").nationalId("NAT2").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c2.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse accReceiver = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c2.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("1000.00")).build());

        // Perform Transfer
        TransactionDto.TransactionResponse txRes = transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(accSender.getAccountNumber())
                .destinationAccountNumber(accReceiver.getAccountNumber())
                .amount(new BigDecimal("1500.00"))
                .transferType(TransactionType.INTERNAL_TRANSFER)
                .description("Gift payment")
                .build());

        assertThat(txRes.getStatus()).isEqualTo(com.bank.enterprise.common.TransactionStatus.COMPLETED);

        // Verify Balances
        AccountDto.BalanceResponse b1 = accountService.checkBalance(accSender.getAccountNumber());
        AccountDto.BalanceResponse b2 = accountService.checkBalance(accReceiver.getAccountNumber());

        assertThat(b1.getAvailableBalance()).isEqualByComparingTo("3500.00");
        assertThat(b2.getAvailableBalance()).isEqualByComparingTo("2500.00");
    }
}
