package com.bank.enterprise.integration;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.AmlScreeningEntity;
import com.bank.enterprise.model.FixedDepositEntity;
import com.bank.enterprise.service.*;
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
class CompleteBankingE2EIntegrationTest {

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
    private ComplianceService complianceService;

    @Autowired
    private AnalyticsService analyticsService;

    @Test
    @DisplayName("E2E Integration: Customer Onboarding -> KYC -> Bank Account -> Loan -> Fixed Deposit -> AML Screening -> Credit Score")
    void fullEnterpriseBankingLifecycle() {
        // 1. User Registration
        UserDto.AuthResponse user = authService.registerUser(UserDto.RegisterRequest.builder()
                .username("e2e_enterprise_user")
                .password("SecurePass123#")
                .email("e2e@enterprisebank.com")
                .phoneNumber("18005559999")
                .role(UserRole.CUSTOMER)
                .build());

        // 2. Customer Profile
        CustomerDto.CustomerResponse customer = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder()
                .userId(user.getUserId())
                .firstName("Enterprise")
                .lastName("Customer")
                .dateOfBirth(LocalDate.of(1982, 11, 4))
                .taxIdNumber("TAX-E2E-999")
                .nationalId("NAT-E2E-999")
                .addressLine1("1 Financial Plaza")
                .city("New York")
                .state("NY")
                .postalCode("10005")
                .build());

        // 3. KYC Verification
        customerService.updateKycStatus(customer.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        // 4. Open Primary Savings Account
        AccountDto.AccountResponse account = accountService.createAccount(AccountDto.AccountCreateRequest.builder()
                .customerId(customer.getCustomerId())
                .accountType(AccountType.SAVINGS)
                .currency(Currency.USD)
                .initialDeposit(new BigDecimal("50000.00"))
                .build());

        // 5. Apply for Personal Loan
        LoanDto.LoanResponse loan = loanService.applyForLoan(LoanDto.LoanApplicationRequest.builder()
                .customerId(customer.getCustomerId())
                .loanType(com.bank.enterprise.common.LoanType.PERSONAL)
                .principalAmount(new BigDecimal("15000.00"))
                .tenureMonths(24)
                .build());

        // Approve and Disburse Loan
        LoanDto.LoanResponse approvedLoan = loanService.approveLoan(loan.getLoanId());
        loanService.disburseLoan(approvedLoan.getLoanId(), account.getAccountNumber());

        // 6. Create Fixed Deposit
        FixedDepositEntity fd = wealthService.createFixedDeposit(customer.getCustomerId(), account.getAccountId(), new BigDecimal("10000.00"), 365);
        assertThat(fd.getFdNumber()).isNotNull();

        // 7. AML Screening
        AmlScreeningEntity aml = complianceService.screenCustomer(customer.getCustomerId());
        assertThat(aml.getRiskLevel()).isEqualTo("LOW");

        // 8. Analytics Credit Score Calculation
        int creditScore = analyticsService.getCustomerCreditScore(customer.getCustomerId());
        assertThat(creditScore).isGreaterThanOrEqualTo(300).isLessThanOrEqualTo(850);
    }
}
