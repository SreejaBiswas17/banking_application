package com.bank.enterprise.service;

import com.bank.enterprise.dto.AuditDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditService {
    void logAction(String action, String performedBy, String entityName, String entityId, String oldState, String newState);
    Page<AuditDto.AuditLogResponse> getLogsByPerformer(String performedBy, Pageable pageable);
    Page<AuditDto.AuditLogResponse> getAllAuditLogs(Pageable pageable);
}
