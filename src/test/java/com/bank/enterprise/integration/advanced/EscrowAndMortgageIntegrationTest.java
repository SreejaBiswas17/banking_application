package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.LoanDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.EscrowAgreementEntity;
import com.bank.enterprise.model.MortgagePropertyEntity;
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
class EscrowAndMortgageIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private LoanService loanService;

    @Autowired
    private MortgageUnderwritingService mortgageService;

    @Autowired
    private EscrowService escrowService;

    @Test
    @DisplayName("Complete Escrow & Mortgage Integration: Setup Buyer/Seller -> Escrow Custody -> Home Loan & Mortgage Property Registration")
    void fullEscrowMortgageLifecycle() {
        // Setup Buyer Customer & Account
        UserDto.AuthResponse u1 = authService.registerUser(UserDto.RegisterRequest.builder().username("buyer_user").password("Pass123#").email("buyer@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c1 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u1.getUserId()).firstName("Buyer").lastName("One").dateOfBirth(LocalDate.of(1985, 1, 1)).taxIdNumber("TAX-B1").nationalId("NAT-B1").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c1.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse buyerAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c1.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("200000.00")).build());

        // Setup Seller Customer & Account
        UserDto.AuthResponse u2 = authService.registerUser(UserDto.RegisterRequest.builder().username("seller_user").password("Pass123#").email("seller@t.com").phoneNumber("222").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c2 = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u2.getUserId()).firstName("Seller").lastName("Two").dateOfBirth(LocalDate.of(1985, 1, 1)).taxIdNumber("TAX-S2").nationalId("NAT-S2").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c2.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse sellerAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c2.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("10000.00")).build());

        // Setup Escrow Custody Account
        AccountDto.AccountResponse escrowAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c1.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("150000.00")).build());
        EscrowAgreementEntity escrow = escrowService.setupEscrowAgreement(c1.getCustomerId(), c2.getCustomerId(), escrowAcc.getAccountId(), new BigDecimal("150000.00"), Currency.USD);
        assertThat(escrow.getStatus()).isEqualTo("FUNDED");

        // Release Escrow Milestone
        escrowService.releaseEscrowFunds(escrow.getEscrowId(), new BigDecimal("50000.00"), sellerAcc.getAccountNumber());
        assertThat(escrowService.setupEscrowAgreement(c1.getCustomerId(), c2.getCustomerId(), escrowAcc.getAccountId(), new BigDecimal("50000.00"), Currency.USD)).isNotNull();

        // Apply for Mortgage Home Loan
        LoanDto.LoanResponse homeLoan = loanService.applyForLoan(LoanDto.LoanApplicationRequest.builder().customerId(c1.getCustomerId()).loanType(com.bank.enterprise.common.LoanType.HOME).principalAmount(new BigDecimal("400000.00")).tenureMonths(360).build());

        // Register Mortgaged Property
        MortgagePropertyEntity property = mortgageService.registerMortgageProperty(homeLoan.getLoanId(), "500 Ocean Drive, Miami FL", new BigDecimal("500000.00"), "SINGLE_FAMILY");
        assertThat(property.getLtvRatio()).isEqualByComparingTo("0.8000"); // 80% LTV
    }
}
