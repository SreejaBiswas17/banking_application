package com.bank.enterprise.repository;

import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.InsurancePolicyEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CorporateAccountRepository extends JpaRepository<CorporateAccountEntity, Long> {
    Optional<CorporateAccountEntity> findByRegistrationNumber(String registrationNumber);
}
