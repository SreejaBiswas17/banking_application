package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.InsurancePolicyEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.InsurancePolicyRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.BancassuranceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BancassuranceServiceImpl implements BancassuranceService {

    private final InsurancePolicyRepository policyRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public InsurancePolicyEntity issuePolicy(Long customerId, String policyType, BigDecimal sumAssured, BigDecimal annualPremium, LocalDate expiryDate) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        String policyNum = "POL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        InsurancePolicyEntity policy = InsurancePolicyEntity.builder()
                .policyNumber(policyNum)
                .customer(customer)
                .policyType(policyType)
                .sumAssured(sumAssured)
                .annualPremium(annualPremium)
                .expiryDate(expiryDate)
                .status("ACTIVE")
                .build();

        InsurancePolicyEntity saved = policyRepository.save(policy);
        auditService.logAction("ISSUE_INSURANCE_POLICY", customer.getUser().getUsername(), "INSURANCE_POLICY", saved.getPolicyId().toString(), null, "Issued: " + policyNum);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<InsurancePolicyEntity> getCustomerPolicies(Long customerId) {
        return policyRepository.findByCustomer_CustomerId(customerId);
    }
}
