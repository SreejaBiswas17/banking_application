package com.bank.enterprise.repository;

import com.bank.enterprise.model.BeneficiaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BeneficiaryRepository extends JpaRepository<BeneficiaryEntity, Long> {

    List<BeneficiaryEntity> findByCustomer_CustomerId(Long customerId);

    Optional<BeneficiaryEntity> findByCustomer_CustomerIdAndBeneficiaryAccountNumber(Long customerId, String accountNumber);
}
