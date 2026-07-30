package com.bank.enterprise.repository;

import com.bank.enterprise.model.ParticipantBankEntity;
import com.bank.enterprise.model.SyndicatedLoanFacilityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SyndicatedLoanRepository extends JpaRepository<SyndicatedLoanFacilityEntity, Long> {
    Optional<SyndicatedLoanFacilityEntity> findByFacilityNumber(String facilityNumber);
}
