package com.bank.enterprise.repository;

import com.bank.enterprise.model.ParticipantBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantBankRepository extends JpaRepository<ParticipantBankEntity, Long> {
    List<ParticipantBankEntity> findByFacility_FacilityId(Long facilityId);
}
