package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import org.junit.jupiter.api.DisplayName;
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
class DomainExceptionAndValidationBoundaryTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Test
    @DisplayName("Should enforce minimum savings deposit threshold of 100.00")
    void savingsAccount_MinimumDepositConstraint() {
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("min_dep_user").password("Pass123#").email("mindep@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Min").lastName("Dep").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-MIN").nationalId("NAT-MIN").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        AccountDto.AccountCreateRequest req = AccountDto.AccountCreateRequest.builder()
                .customerId(c.getCustomerId())
                .accountType(AccountType.SAVINGS)
                .initialDeposit(new BigDecimal("10.00")) // Under minimum 100.00 limit
                .build();

        assertThatThrownBy(() -> accountService.createAccount(req))
                .isInstanceOf(BankException.class)
                .hasMessageContaining("Minimum initial deposit for Savings account is");
    }

    @Test
    @DisplayName("Should enforce age limit of 18 years for opening customer profile")
    void customerProfile_AgeLimitConstraint() {
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("minor_user").password("Pass123#").email("minor@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());

        CustomerDto.CustomerCreateRequest req = CustomerDto.CustomerCreateRequest.builder()
                .userId(u.getUserId())
                .firstName("Minor")
                .lastName("Child")
                .dateOfBirth(LocalDate.now().minusYears(10)) // 10 years old
                .taxIdNumber("TAX-MINOR")
                .nationalId("NAT-MINOR")
                .addressLine1("a")
                .city("c")
                .state("s")
                .postalCode("p")
                .build();

        assertThatThrownBy(() -> customerService.createCustomerProfile(req))
                .isInstanceOf(BankException.class)
                .hasMessageContaining("Customer must be at least 18 years old");
    }
}
