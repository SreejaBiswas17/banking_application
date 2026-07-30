package com.bank.enterprise.repository;

import com.bank.enterprise.model.InsurancePolicyEntity;
import com.bank.enterprise.model.PayrollBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollBatchRepository extends JpaRepository<PayrollBatchEntity, Long> {
    List<PayrollBatchEntity> findByCorporateAccount_CorporateId(Long corporateId);
}
