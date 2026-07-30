package com.bank.enterprise.repository;

import com.bank.enterprise.model.StandingInstructionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StandingInstructionRepository extends JpaRepository<StandingInstructionEntity, Long> {
    List<StandingInstructionEntity> findByIsActiveTrueAndExecutionDayOfMonth(Integer dayOfMonth);
    List<StandingInstructionEntity> findBySourceAccount_AccountId(Long sourceAccountId);
}
