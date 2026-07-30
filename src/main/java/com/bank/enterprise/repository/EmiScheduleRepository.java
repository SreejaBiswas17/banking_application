package com.bank.enterprise.repository;

import com.bank.enterprise.common.EmiStatus;
import com.bank.enterprise.model.EmiScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmiScheduleRepository extends JpaRepository<EmiScheduleEntity, Long> {

    List<EmiScheduleEntity> findByLoan_LoanIdOrderByInstallmentNumberAsc(Long loanId);

    List<EmiScheduleEntity> findByLoan_LoanIdAndStatus(Long loanId, EmiStatus status);

    @Query("SELECT e FROM EmiScheduleEntity e WHERE e.dueDate <= :today AND e.status = 'UNPAID'")
    List<EmiScheduleEntity> findOverdueEmis(@Param("today") LocalDate today);
}
