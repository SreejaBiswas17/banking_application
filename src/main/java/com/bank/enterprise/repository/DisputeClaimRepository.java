package com.bank.enterprise.repository;

import com.bank.enterprise.model.DisputeClaimEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DisputeClaimRepository extends JpaRepository<DisputeClaimEntity, Long> {
    Optional<DisputeClaimEntity> findByClaimNumber(String claimNumber);
    List<DisputeClaimEntity> findByCustomer_CustomerId(Long customerId);
}
