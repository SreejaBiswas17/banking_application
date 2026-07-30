package com.bank.enterprise.service;

import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.InsurancePolicyEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.InsurancePolicyRepository;
import com.bank.enterprise.service.impl.BancassuranceServiceImpl;
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
class BancassuranceServiceTest {

    @Mock
    private InsurancePolicyRepository policyRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private BancassuranceServiceImpl bancassuranceService;

    private CustomerEntity customer;

    @BeforeEach
    void setUp() {
        customer = CustomerEntity.builder().customerId(1L).build();
    }

    @Test
    @DisplayName("Should successfully issue insurance policy")
    void issuePolicy_Success() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        InsurancePolicyEntity policy = InsurancePolicyEntity.builder()
                .policyId(10L)
                .policyNumber("POL-12345678")
                .customer(customer)
                .policyType("HEALTH")
                .sumAssured(new BigDecimal("500000.00"))
                .annualPremium(new BigDecimal("1200.00"))
                .status("ACTIVE")
                .build();

        when(policyRepository.save(any(InsurancePolicyEntity.class))).thenReturn(policy);

        InsurancePolicyEntity result = bancassuranceService.issuePolicy(1L, "HEALTH", new BigDecimal("500000.00"), new BigDecimal("1200.00"), LocalDate.now().plusYears(1));

        assertThat(result).isNotNull();
        assertThat(result.getPolicyNumber()).isEqualTo("POL-12345678");
        assertThat(result.getStatus()).isEqualTo("ACTIVE");
    }
}
