package com.bank.enterprise.repository;

import com.bank.enterprise.model.GeneralLedgerAccountEntity;
import com.bank.enterprise.model.JournalEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GeneralLedgerRepository extends JpaRepository<GeneralLedgerAccountEntity, Long> {
    Optional<GeneralLedgerAccountEntity> findByGlCode(String glCode);
}
