package com.bank.enterprise.service.impl;

import com.bank.enterprise.dto.AuditDto;
import com.bank.enterprise.model.AuditLogEntity;
import com.bank.enterprise.repository.AuditLogRepository;
import com.bank.enterprise.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;

    @Override
    @Async
    @Transactional
    public void logAction(String action, String performedBy, String entityName, String entityId, String oldState, String newState) {
        AuditLogEntity log = AuditLogEntity.builder()
                .action(action)
                .performedBy(performedBy != null ? performedBy : "SYSTEM")
                .entityName(entityName)
                .entityId(entityId)
                .oldState(oldState)
                .newState(newState)
                .build();
        auditLogRepository.save(log);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditDto.AuditLogResponse> getLogsByPerformer(String performedBy, Pageable pageable) {
        return auditLogRepository.findByPerformedBy(performedBy, pageable)
                .map(this::mapToAuditResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditDto.AuditLogResponse> getAllAuditLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable)
                .map(this::mapToAuditResponse);
    }

    private AuditDto.AuditLogResponse mapToAuditResponse(AuditLogEntity entity) {
        return AuditDto.AuditLogResponse.builder()
                .auditId(entity.getAuditId())
                .action(entity.getAction())
                .performedBy(entity.getPerformedBy())
                .entityName(entity.getEntityName())
                .entityId(entity.getEntityId())
                .oldState(entity.getOldState())
                .newState(entity.getNewState())
                .ipAddress(entity.getIpAddress())
                .timestamp(entity.getTimestamp())
                .build();
    }
}
