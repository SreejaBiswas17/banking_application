package com.bank.enterprise.repository;

import com.bank.enterprise.model.AuditLogEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLogEntity, Long> {

    Page<AuditLogEntity> findByPerformedBy(String performedBy, Pageable pageable);

    Page<AuditLogEntity> findByEntityNameAndEntityId(String entityName, String entityId, Pageable pageable);

    Page<AuditLogEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
