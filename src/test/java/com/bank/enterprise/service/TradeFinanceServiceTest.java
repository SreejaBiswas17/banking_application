package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.LetterOfCreditEntity;
import com.bank.enterprise.model.UserEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.LetterOfCreditRepository;
import com.bank.enterprise.service.impl.TradeFinanceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradeFinanceServiceTest {

    @Mock
    private LetterOfCreditRepository lcRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private TradeFinanceServiceImpl tradeFinanceService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        UserEntity user = UserEntity.builder().userId(1L).username("corp_user").build();
        customer = CustomerEntity.builder().customerId(10L).user(user).kycStatus(KycStatus.VERIFIED).build();
    }

    @Test
    @DisplayName("Should successfully issue Letter of Credit")
    void issueLc_Success() {
        when(customerRepository.findById(10L)).thenReturn(Optional.of(customer));

        LetterOfCreditEntity lc = LetterOfCreditEntity.builder()
                .lcId(100L)
                .lcNumber("LC-12345678")
                .applicantCustomer(customer)
                .beneficiaryName("Global Exports Ltd")
                .advisingBankSwift("CITIUS33XXXX")
                .amount(new BigDecimal("100000.00"))
                .currency(Currency.USD)
                .expiryDate(LocalDate.now().plusMonths(6))
                .status("ISSUED")
                .build();

        when(lcRepository.save(any(LetterOfCreditEntity.class))).thenReturn(lc);

        LetterOfCreditEntity result = tradeFinanceService.issueLetterOfCredit(10L, "Global Exports Ltd", "CITIUS33XXXX", new BigDecimal("100000.00"), Currency.USD, LocalDate.now().plusMonths(6));

        assertThat(result).isNotNull();
        assertThat(result.getLcNumber()).isEqualTo("LC-12345678");
        assertThat(result.getStatus()).isEqualTo("ISSUED");
    }
}
