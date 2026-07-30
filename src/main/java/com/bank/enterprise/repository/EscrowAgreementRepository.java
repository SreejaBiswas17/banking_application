package com.bank.enterprise.repository;

import com.bank.enterprise.model.EscrowAgreementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EscrowAgreementRepository extends JpaRepository<EscrowAgreementEntity, Long> {
    Optional<EscrowAgreementEntity> findByEscrowNumber(String escrowNumber);
}
