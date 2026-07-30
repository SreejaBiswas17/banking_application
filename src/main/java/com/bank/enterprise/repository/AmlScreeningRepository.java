package com.bank.enterprise.repository;

import com.bank.enterprise.model.AmlScreeningEntity;
import com.bank.enterprise.model.FraudLogEntity;
import com.bank.enterprise.model.RegulatoryReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AmlScreeningRepository extends JpaRepository<AmlScreeningEntity, Long> {
    List<AmlScreeningEntity> findByCustomer_CustomerId(Long customerId);
    List<AmlScreeningEntity> findByRiskLevel(String riskLevel);
}
