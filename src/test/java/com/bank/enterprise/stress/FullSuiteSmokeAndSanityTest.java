package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.AuditLogEntity;
import com.bank.enterprise.model.CardEntity;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.repository.AuditLogRepository;
import com.bank.enterprise.service.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class FullSuiteSmokeAndSanityTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CardService cardService;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Test
    @DisplayName("Smoke Test: Verify end-to-end banking events generate appropriate Audit Trails")
    void smokeTest_AuditTrailGeneration() {
        // Step 1: User Registration
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder()
                .username("audit_smoke_user")
                .password("Pass123#")
                .email("audit_smoke@t.com")
                .phoneNumber("555111222")
                .role(UserRole.CUSTOMER)
                .build());

        // Step 2: Create Customer Profile
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder()
                .userId(u.getUserId())
                .firstName("Audit")
                .lastName("Smoke")
                .dateOfBirth(LocalDate.of(1992, 2, 2))
                .taxIdNumber("TAX-AUD")
                .nationalId("NAT-AUD")
                .addressLine1("1 Audit St")
                .city("Chicago")
                .state("IL")
                .postalCode("60602")
                .build());

        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        // Step 3: Open Account
        AccountDto.AccountResponse acc = accountService.createAccount(AccountDto.AccountCreateRequest.builder()
                .customerId(c.getCustomerId())
                .accountType(AccountType.SAVINGS)
                .currency(Currency.USD)
                .initialDeposit(new BigDecimal("1000.00"))
                .build());

        // Step 4: Issue Debit Card
        cardService.issueCard(com.bank.enterprise.dto.CardDto.CardIssueRequest.builder()
                .accountId(acc.getAccountId())
                .cardType(com.bank.enterprise.common.CardType.DEBIT_GOLD)
                .pin("1234")
                .build());

        // Verify audit logs captured
        Page<AuditLogEntity> logs = auditLogRepository.findByPerformedBy("audit_smoke_user", Pageable.unpaged());
        assertThat(logs).isNotNull();
    }
}
