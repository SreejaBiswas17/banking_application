package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.exception.InsufficientBalanceException;
import com.bank.enterprise.exception.InvalidTransactionException;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ComprehensiveRegressionTestSuite {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Nested
    @DisplayName("Transaction Limits and Exception Boundaries")
    class TransactionBoundaryTests {

        @Test
        @DisplayName("Should reject transfer when exceeding available overdraft balance")
        void rejectTransfer_ExceedingOverdraft() {
            UserDto.AuthResponse u1 = authService.registerUser(UserDto.RegisterRequest.builder().username("overdraft_user").password("Pass123#").email("od@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
            CustomerDto.CustomerResponse c1 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u1.getUserId()).firstName("Over").lastName("Draft").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-OD").nationalId("NAT-OD").addressLine1("a").city("c").state("s").postalCode("p").build());
            customerService.updateKycStatus(c1.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
            AccountDto.AccountResponse acc1 = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c1.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("100.00")).build());

            UserDto.AuthResponse u2 = authService.registerUser(UserDto.RegisterRequest.builder().username("target_user").password("Pass123#").email("target@t.com").phoneNumber("222").role(UserRole.CUSTOMER).build());
            CustomerDto.CustomerResponse c2 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u2.getUserId()).firstName("Tar").lastName("Get").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-TG").nationalId("NAT-TG").addressLine1("a").city("c").state("s").postalCode("p").build());
            customerService.updateKycStatus(c2.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
            AccountDto.AccountResponse acc2 = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c2.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("100.00")).build());

            TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                    .sourceAccountNumber(acc1.getAccountNumber())
                    .destinationAccountNumber(acc2.getAccountNumber())
                    .amount(new BigDecimal("10000.00")) // Exceeds balance + 1000 overdraft limit
                    .build();

            assertThatThrownBy(() -> transactionService.transferFunds(req))
                    .isInstanceOf(InsufficientBalanceException.class);
        }

        @Test
        @DisplayName("Should prevent transfer between identical source and destination account numbers")
        void rejectSelfTransfer() {
            TransactionDto.TransferRequest req = TransactionDto.TransferRequest.builder()
                    .sourceAccountNumber("1000100010")
                    .destinationAccountNumber("1000100010")
                    .amount(new BigDecimal("50.00"))
                    .build();

            assertThatThrownBy(() -> transactionService.transferFunds(req))
                    .isInstanceOf(InvalidTransactionException.class)
                    .hasMessageContaining("Source and destination accounts cannot be identical");
        }
    }
}
