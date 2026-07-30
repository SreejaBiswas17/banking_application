package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.FixedDepositEntity;
import com.bank.enterprise.service.*;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SystemArchitectureVerificationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private WealthManagementService wealthService;

    @Autowired
    private ReportService reportService;

    @Nested
    @DisplayName("Financial Summary and Statement Regression Tests")
    class FinancialSummaryTests {

        @Test
        @DisplayName("Should generate valid bank-wide financial summary report")
        void generateFinancialSummary_Success() {
            // Setup Customer & Account & Loan
            UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("arch_user").password("Pass123#").email("arch@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
            CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Arch").lastName("Tester").dateOfBirth(LocalDate.of(1985, 5, 5)).taxIdNumber("TAX-ARCH").nationalId("NAT-ARCH").addressLine1("a").city("c").state("s").postalCode("p").build());
            customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

            AccountDto.AccountResponse acc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("25000.00")).build());

            LoanDto.LoanResponse loan = loanService.applyForLoan(LoanDto.LoanApplicationRequest.builder().customerId(c.getCustomerId()).loanType(com.bank.enterprise.common.LoanType.AUTO).principalAmount(new BigDecimal("20000.00")).tenureMonths(36).build());

            FixedDepositEntity fd = wealthService.createFixedDeposit(c.getCustomerId(), acc.getAccountId(), new BigDecimal("5000.00"), 180);

            var summary = reportService.generateFinancialSummary();

            assertThat(summary).isNotNull();
            assertThat(summary.getTotalCustomers()).isGreaterThanOrEqualTo(1);
            assertThat(summary.getTotalAccounts()).isGreaterThanOrEqualTo(1);
            assertThat(summary.getTotalBankDeposits()).isGreaterThanOrEqualTo(new BigDecimal("20000.00"));
        }
    }
}
