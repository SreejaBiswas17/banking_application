package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.ReportService;
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
class SystemArchitectureVerificationTest2 {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ReportService reportService;

    @Test
    @DisplayName("Generate Account Statement for 30-Day Period Verification")
    void testAccountStatementGeneration() {
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("stmt_user").password("Pass123#").email("stmt@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Stmt").lastName("Tester").dateOfBirth(LocalDate.of(1985, 5, 5)).taxIdNumber("TAX-STMT").nationalId("NAT-STMT").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        AccountDto.AccountResponse acc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("1000.00")).build());

        var stmt = reportService.generateAccountStatement(acc.getAccountNumber(), LocalDate.now().minusDays(30), LocalDate.now());
        assertThat(stmt).isNotNull();
        assertThat(stmt.getAccountNumber()).isEqualTo(acc.getAccountNumber());
    }
}
