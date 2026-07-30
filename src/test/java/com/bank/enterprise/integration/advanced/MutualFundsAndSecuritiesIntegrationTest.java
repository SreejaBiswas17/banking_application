package com.bank.enterprise.integration.advanced;

import com.bank.enterprise.common.AccountType;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.common.UserRole;
import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.CustomerDto;
import com.bank.enterprise.dto.UserDto;
import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;
import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;
import com.bank.enterprise.repository.MutualFundSchemeRepository;
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
class MutualFundsAndSecuritiesIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private MutualFundSchemeRepository schemeRepository;

    @Autowired
    private SecuritiesTradingService securitiesService;

    @Test
    @DisplayName("Complete Wealth & Equities Integration: Buy Mutual Funds -> Open Demat -> Trade Stocks")
    void fullWealthEquitiesLifecycle() {
        // Setup Customer & Account
        UserDto.AuthResponse user = authService.registerUser(UserDto.RegisterRequest.builder().username("investor_pro").password("Pass123#").email("investor@t.com").phoneNumber("999").role(UserRole.CUSTOMER).build());
        CustomerDto.CustomerResponse cust = customerService.createCustomerProfile(CustomerDto.CustomerCreateRequest.builder().userId(user.getUserId()).firstName("Investor").lastName("Pro").dateOfBirth(LocalDate.of(1987, 4, 10)).taxIdNumber("TAX-INV").nationalId("NAT-INV").addressLine1("a").city("c").state("s").postalCode("p").build());
        customerService.updateKycStatus(cust.getCustomerId(), CustomerDto.KycUpdateDto.builder().status(KycStatus.VERIFIED).build());
        AccountDto.AccountResponse acc = accountService.createAccount(AccountDto.AccountCreateRequest.builder().customerId(cust.getCustomerId()).accountType(AccountType.SAVINGS).initialDeposit(new BigDecimal("100000.00")).build());

        // Save Seed Scheme
        MutualFundSchemeEntity scheme = schemeRepository.save(MutualFundSchemeEntity.builder().schemeCode("INDEX_500").schemeName("S&P 500 Index Fund").category("EQUITY").currentNav(new BigDecimal("100.00")).riskRating("HIGH").build());

        // Purchase Mutual Fund Units
        MutualFundHoldingEntity mfHolding = investmentService.buyMutualFundUnits(cust.getCustomerId(), acc.getAccountId(), "INDEX_500", new BigDecimal("10000.00"));
        assertThat(mfHolding.getTotalUnits()).isEqualByComparingTo("100.0000");

        // Open Demat Trading Account
        SecuritiesPortfolioEntity demat = securitiesService.openDematPortfolio(cust.getCustomerId());
        assertThat(demat.getDematAccountNumber()).contains("DEMAT");

        // Trade Stock (Buy AAPL)
        StockOrderEntity stockOrder = securitiesService.executeStockTrade(cust.getCustomerId(), acc.getAccountId(), "AAPL", "BUY", 20, new BigDecimal("150.00"));
        assertThat(stockOrder.getStatus()).isEqualTo("EXECUTED");
    }
}
