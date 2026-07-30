package com.bank.enterprise.controller;

import com.bank.enterprise.common.ApiResponse;
import com.bank.enterprise.model.SupportTicketEntity;
import com.bank.enterprise.model.TicketMessageEntity;
import com.bank.enterprise.service.CustomerSupportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/support")
@RequiredArgsConstructor
@Tag(name = "Customer Support & Ticketing", description = "Endpoints for Customer Helpdesk & Issue Escalation")
public class CustomerSupportController {

    private final CustomerSupportService supportService;

    @PostMapping("/tickets")
    @Operation(summary = "Open a new support ticket")
    public ResponseEntity<ApiResponse<SupportTicketEntity>> createTicket(
            @RequestParam Long customerId,
            @RequestParam String subject,
            @RequestParam String category,
            @RequestParam String priority,
            @RequestParam String message) {
        return ResponseEntity.ok(ApiResponse.success(supportService.createTicket(customerId, subject, category, priority, message), "Ticket created"));
    }

    @PostMapping("/tickets/{ticketId}/reply")
    @Operation(summary = "Add message reply to ticket")
    public ResponseEntity<ApiResponse<TicketMessageEntity>> reply(
            @PathVariable Long ticketId,
            @RequestParam String senderName,
            @RequestParam String senderRole,
            @RequestParam String message) {
        return ResponseEntity.ok(ApiResponse.success(supportService.replyToTicket(ticketId, senderName, senderRole, message)));
    }

    @GetMapping("/tickets/customer/{customerId}")
    @Operation(summary = "Get support tickets for customer")
    public ResponseEntity<ApiResponse<List<SupportTicketEntity>>> getCustomerTickets(@PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(supportService.getCustomerTickets(customerId)));
    }

    @GetMapping("/tickets/{ticketId}/messages")
    @Operation(summary = "Get message log for ticket")
    public ResponseEntity<ApiResponse<List<TicketMessageEntity>>> getTicketMessages(@PathVariable Long ticketId) {
        return ResponseEntity.ok(ApiResponse.success(supportService.getTicketMessages(ticketId)));
    }
}
