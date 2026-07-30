package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.common.PageResponse;
import com.bank.enterprise.dto.AuditDto;
import com.bank.enterprise.service.AuditService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/audit-logs")
@RequiredArgsConstructor
@Tag(name = "Audit & Compliance", description = "Endpoints for Security Auditing and Trail Tracking")
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    @Operation(summary = "Get all audit logs (Paginated)")
    public ResponseEntity<ApiResponse<PageResponse<AuditDto.AuditLogResponse>>> getAllAuditLogs(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(auditService.getAllAuditLogs(pageable))));
    }

    @GetMapping("/user/{performedBy}")
    @Operation(summary = "Get audit logs performed by user")
    public ResponseEntity<ApiResponse<PageResponse<AuditDto.AuditLogResponse>>> getLogsByPerformer(@PathVariable String performedBy, Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(PageResponse.from(auditService.getLogsByPerformer(performedBy, pageable))));
    }
}
