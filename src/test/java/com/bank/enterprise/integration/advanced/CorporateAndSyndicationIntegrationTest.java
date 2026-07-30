package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.ParticipantBankEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import com.bank.enterprise.model.SyndicatedLoanFacilityEntity;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CorporateBankingService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.SyndicatedLoanService;
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
class CorporateAndSyndicationIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private CorporateBankingService corporateService;

    @Autowired
    private SyndicatedLoanService syndicationService;

    @Test
    @DisplayName("Complete Corporate Integration: Register Corporate -> Execute Payroll -> Setup Syndicated Loan Facility -> Add Participants")
    void fullCorporateSyndicationLifecycle() {
        // Setup Corporate Rep User & Primary Account
        UserDto.AuthResponse user = authService.registerUser(UserDto.RegisterRequest.builder().username("corp_rep").password("Pass123#").email("corp@t.com").phoneNumber("111").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse cust = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(user.getUserId()).firstName("Corp").lastName("Admin").dateOfBirth(LocalDate.of(1980, 1, 1)).taxIdNumber("TAX-CORP").nationalId("NAT-CORP").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(cust.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse primaryAcc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(cust.getCustomerId()).accountType(AccountType.CHECKING).initialDeposit(new BigDecimal("500000.00")).build());

        // Register Corporate Account
        CorporateAccountEntity corp = corporateService.registerCorporateAccount("Apex Global Corp", "REG-APEX-99", "TAX-APEX-99", primaryAcc.getAccountId(), new BigDecimal("1000000.00"));
        assertThat(corp.getCorporateId()).isNotNull();

        // Execute Payroll Batch
        PayrollBatchEntity payroll = corporateService.executePayrollBatch(corp.getCorporateId(), 150, new BigDecimal("150000.00"));
        assertThat(payroll.getStatus()).isEqualTo("COMPLETED");

        // Setup Syndicated Loan Facility
        SyndicatedLoanFacilityEntity facility = syndicationService.createFacility(corp.getCorporateId(), new BigDecimal("10000000.00"), "JPMorgan Chase", Currency.USD, LocalDate.now().plusYears(5));
        assertThat(facility.getFacilityNumber()).isNotNull();

        // Add Participant Bank 1
        ParticipantBankEntity part1 = syndicationService.addParticipantBank(facility.getFacilityId(), "Citigroup Inc", "CITIUS33XXX", new BigDecimal("4000000.00"));
        assertThat(part1.getCommittedAmount()).isEqualByComparingTo("4000000.00");

        // Add Participant Bank 2
        ParticipantBankEntity part2 = syndicationService.addParticipantBank(facility.getFacilityId(), "Bank of America", "BOFAUS3NXXX", new BigDecimal("6000000.00"));
        assertThat(part2.getCommittedAmount()).isEqualByComparingTo("6000000.00");
    }
}
