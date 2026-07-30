package com.bank.enterprise.repository;

import com.bank.enterprise.model.FraudLogEntity;
import com.bank.enterprise.model.RegulatoryReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FraudLogRepository extends JpaRepository<FraudLogEntity, Long> {
    List<FraudLogEntity> findByStatus(String status);
}
