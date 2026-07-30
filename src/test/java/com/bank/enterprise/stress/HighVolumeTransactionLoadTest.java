package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
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
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class HighVolumeTransactionLoadTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Test
    @DisplayName("Load Benchmark: Execute 50 Sequential Transfers and Verify Statement Ledger Consistency")
    void executeHighVolumeTransfers() {
        // Setup Customer A
        UserDto.AuthResponse u1 = authService.registerUser(UserDto.RegisterRequest.builder().username("load_sender").password("Pass123#").email("load1@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c1 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u1.getUserId()).firstName("Load").lastName("Sender").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-L1").nationalId("NAT-L1").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c1.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse senderAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c1.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("100000.00")).build());

        // Setup Customer B
        UserDto.AuthResponse u2 = authService.registerUser(UserDto.RegisterRequest.builder().username("load_receiver").password("Pass123#").email("load2@t.com").phoneNumber("222").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c2 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u2.getUserId()).firstName("Load").lastName("Receiver").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-L2").nationalId("NAT-L2").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c2.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse receiverAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c2.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("5000.00")).build());

        List<TransactionDto.TransactionResponse> responses = new ArrayList<>();
        BigDecimal transferAmount = new BigDecimal("10.00");

        for (int i = 0; i < 50; i++) {
            TransactionDto.TransactionResponse res = transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                    .sourceAccountNumber(senderAcc.getAccountNumber())
                    .destinationAccountNumber(receiverAcc.getAccountNumber())
                    .amount(transferAmount)
                    .description("Sequential Load Transfer #" + (i + 1))
                    .build());
            responses.add(res);
        }

        assertThat(responses).hasSize(50);

        AccountDto.BalanceResponse b1 = accountService.checkBalance(senderAcc.getAccountNumber());
        AccountDto.BalanceResponse b2 = accountService.checkBalance(receiverAcc.getAccountNumber());

        assertThat(b1.getAvailableBalance()).isEqualByComparingTo("99500.00");
        assertThat(b2.getAvailableBalance()).isEqualByComparingTo("5500.00");
    }
}
