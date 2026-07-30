package com.bank.enterprise.repository;

import com.bank.enterprise.model.MutualFundHoldingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MutualFundHoldingRepository extends JpaRepository<MutualFundHoldingEntity, Long> {
    List<MutualFundHoldingEntity> findByCustomer_CustomerId(Long customerId);
    Optional<MutualFundHoldingEntity> findByCustomer_CustomerIdAndScheme_SchemeId(Long customerId, Long schemeId);
}
