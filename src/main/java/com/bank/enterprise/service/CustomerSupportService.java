package com.bank.enterprise.service;

import com.bank.enterprise.model.SupportTicketEntity;
import com.bank.enterprise.model.TicketMessageEntity;

import java.util.List;

public interface CustomerSupportService {
    SupportTicketEntity createTicket(Long customerId, String subject, String category, String priority, String initialMessage);
    TicketMessageEntity replyToTicket(Long ticketId, String senderName, String senderRole, String messageText);
    SupportTicketEntity resolveTicket(Long ticketId);
    List<SupportTicketEntity> getCustomerTickets(Long customerId);
    List<TicketMessageEntity> getTicketMessages(Long ticketId);
}
