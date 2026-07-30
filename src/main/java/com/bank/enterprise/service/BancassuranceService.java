package com.bank.enterprise.service;

import com.bank.enterprise.model.InsurancePolicyEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface BancassuranceService {
    InsurancePolicyEntity issuePolicy(Long customerId, String policyType, BigDecimal sumAssured, BigDecimal annualPremium, LocalDate expiryDate);
    List<InsurancePolicyEntity> getCustomerPolicies(Long customerId);
}
