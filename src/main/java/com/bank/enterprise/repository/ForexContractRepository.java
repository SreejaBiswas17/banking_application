package com.bank.enterprise.repository;

import com.bank.enterprise.model.ForexContractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ForexContractRepository extends JpaRepository<ForexContractEntity, Long> {
    Optional<ForexContractEntity> findByContractNumber(String contractNumber);
    List<ForexContractEntity> findByCustomer_CustomerId(Long customerId);
}
