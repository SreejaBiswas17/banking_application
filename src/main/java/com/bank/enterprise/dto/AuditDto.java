package com.bank.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuditDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AuditLogResponse {
        private Long auditId;
        private String action;
        private String performedBy;
        private String entityName;
        private String entityId;
        private String oldState;
        private String newState;
        private String ipAddress;
        private LocalDateTime timestamp;
    }
}
