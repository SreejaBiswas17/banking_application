package com.bank.enterprise.repository;

import com.bank.enterprise.model.FedWirePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FedWireRepository extends JpaRepository<FedWirePaymentEntity, Long> {
    Optional<FedWirePaymentEntity> findByImadNumber(String imadNumber);
}
