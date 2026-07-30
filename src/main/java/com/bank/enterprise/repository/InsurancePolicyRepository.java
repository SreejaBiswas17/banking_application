package com.bank.enterprise.repository;

import com.bank.enterprise.model.InsurancePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicyEntity, Long> {
    Optional<InsurancePolicyEntity> findByPolicyNumber(String policyNumber);
    List<InsurancePolicyEntity> findByCustomer_CustomerId(Long customerId);
}
