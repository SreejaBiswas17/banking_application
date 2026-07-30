package com.bank.enterprise.repository;

import com.bank.enterprise.model.FixedDepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FixedDepositRepository extends JpaRepository<FixedDepositEntity, Long> {
    List<FixedDepositEntity> findByCustomer_CustomerId(Long customerId);
    List<FixedDepositEntity> findByIsClosedFalseAndMaturityDateLessThanEqual(LocalDate date);
}
