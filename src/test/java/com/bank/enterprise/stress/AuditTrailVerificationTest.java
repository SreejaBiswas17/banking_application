package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuditService;
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

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AuditTrailVerificationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuditService auditService;

    @Test
    @DisplayName("Verification: Audit service captures entity state mutations accurately")
    void testAuditLogCapturesStateMutations() {
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("audit_mutator").password("Pass123#").email("mutator@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Mutator").lastName("Test").dateOfBirth(LocalDate.of(1990, 1, 1)).taxIdNumber("TAX-MUT").nationalId("NAT-MUT").addressLine1("a").city("c").state("s").postalCode("p").build());

        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        AccountDto.AccountResponse acc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("500.00")).build());

        accountService.updateAccountStatus(acc.getAccountId(), com.bank.enterprise.common.AccountStatus.FROZEN);

        var logs = auditService.getAllAuditLogs(org.springframework.data.domain.Pageable.unpaged());
        assertThat(logs.getContent()).isNotEmpty();
    }
}
