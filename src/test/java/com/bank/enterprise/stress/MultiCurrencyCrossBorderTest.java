package com.bank.enterprise.stress;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.FedWirePaymentEntity;
import com.bank.enterprise.model.ForexContractEntity;
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
class MultiCurrencyCrossBorderTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private ForexService forexService;

    @Autowired
    private WireTransferService wireService;

    @Test
    @DisplayName("Cross-Border Test: Book FX Spot (EUR to USD) -> Settle via FedWire RTGS")
    void executeCrossBorderSettlement() {
        // Setup Customer & EUR Account
        UserDto.AuthResponse u = authService.registerUser(UserDto.RegisterRequest.builder().username("global_trader").password("Pass123#").email("global@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse c = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(u.getUserId()).firstName("Global").lastName("Trader").dateOfBirth(LocalDate.of(1980, 1, 1)).taxIdNumber("TAX-GLOB").nationalId("NAT-GLOB").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(c.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse accEUR = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(c.getCustomerId()).accountType(AccountType.CHECKING).currency(Currency.EUR).initialDeposit(new BigDecimal("500000.00")).build());

        // Book Forex Contract EUR -> USD
        ForexContractEntity fx = forexService.bookContract(c.getCustomerId(), Currency.EUR, Currency.USD, new BigDecimal("100000.00"));
        assertThat(fx.getContractNumber()).isNotNull();

        // Transmit Wire Payment
        FedWirePaymentEntity wire = wireService.executeFedWireTransfer(accEUR.getAccountId(), "021000021", "GB99BARC2020153000", "Barclays Bank UK", new BigDecimal("108500.00"), Currency.USD);
        assertThat(wire.getImadNumber()).isNotNull();
        assertThat(wire.getStatus()).isEqualTo("CLEARED");
    }
}
