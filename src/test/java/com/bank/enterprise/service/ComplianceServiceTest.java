package com.bank.enterprise.service;

import com.bank.enterprise.model.AmlScreeningEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.repository.AmlScreeningRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.FraudLogRepository;
import com.bank.enterprise.repository.RegulatoryReportRepository;
import com.bank.enterprise.repository.TransactionRepository;
import com.bank.enterprise.service.impl.ComplianceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceTest {

    @Mock
    private AmlScreeningRepository amlRepository;

    @Mock
    private FraudLogRepository fraudRepository;

    @Mock
    private RegulatoryReportRepository reportRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private ComplianceServiceImpl complianceService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder()
                .customerId(1L)
                .firstName("Compliance")
                .lastName("User")
                .country("USA")
                .build();
    }

    @Test
    @DisplayName("Should successfully screen customer for AML risk")
    void screenCustomer_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        AmlScreeningEntity aml = AmlScreeningEntity.builder()
                .screeningId(10L)
                .customer(customer)
                .riskScore(new BigDecimal("15.50"))
                .riskLevel("LOW")
                .isPep(false)
                .isSanctionMatch(false)
                .build();

        when(amlRepository.save(any(AmlScreeningEntity.class))).thenReturn(aml);

        AmlScreeningEntity result = complianceService.screenCustomer(1L);

        assertThat(result).isNotNull();
        assertThat(result.getRiskLevel()).isEqualTo("LOW");
        verify(amlRepository, times(1)).save(any());
    }
}
