package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.ForexContractEntity;
import com.bank.enterprise.model.MoneyMarketDealEntity;
import com.bank.enterprise.model.TreasuryBondEntity;
import com.bank.enterprise.service.AuthService;
import com.bank.enterprise.service.CustomerService;
import com.bank.enterprise.service.ForexService;
import com.bank.enterprise.service.TreasuryManagementService;
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
class TreasuryAndForexIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ForexService forexService;

    @Autowired
    private TreasuryManagementService treasuryService;

    @Test
    @DisplayName("Complete Treasury Integration: Book Forex Spot -> Purchase Sovereign Bond -> Execute Interbank Money Market Deal")
    void fullTreasuryForexLifecycle() {
        // Setup Customer
        UserDto.AuthResponse user = authService.registerUser(UserDto.RegisterRequest.builder().username("fx_trader").password("Pass123#").email("fx@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse cust = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(user.getUserId()).firstName("FX").lastName("Trader").dateOfBirth(LocalDate.of(1985, 5, 5)).taxIdNumber("TAX-FX").nationalId("NAT-FX").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(cust.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());

        // Book Forex Spot Contract
        ForexContractEntity fx = forexService.bookContract(cust.getCustomerId(), Currency.EUR, Currency.USD, new BigDecimal("100000.00"));
        assertThat(fx.getContractNumber()).isNotNull();
        assertThat(fx.getStatus()).isEqualTo("BOOKED");

        // Purchase Treasury Bond
        TreasuryBondEntity bond = treasuryService.purchaseBond("US912828ZD95", "US Department of Treasury", new BigDecimal("500000.00"), new BigDecimal("0.0450"), Currency.USD, LocalDate.now().plusYears(10), "HTM");
        assertThat(bond.getBondId()).isNotNull();

        // Execute Interbank Money Market Lending Deal
        MoneyMarketDealEntity deal = treasuryService.executeInterbankDeal("Barclays Bank PLC", "INTERBANK_LENDING", new BigDecimal("2000000.00"), new BigDecimal("0.0525"), Currency.USD, LocalDate.now(), LocalDate.now().plusMonths(3));
        assertThat(deal.getDealReference()).isNotNull();
    }
}
