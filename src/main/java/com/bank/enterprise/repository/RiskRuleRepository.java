package com.bank.enterprise.repository;

import com.bank.enterprise.model.RiskRuleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskRuleRepository extends JpaRepository<RiskRuleEntity, Long> {
    List<RiskRuleEntity> findByIsActiveTrue();
}
