package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.SupportTicketEntity;
import com.bank.enterprise.model.TicketMessageEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.SupportTicketRepository;
import com.bank.enterprise.repository.TicketMessageRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.CustomerSupportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerSupportServiceImpl implements CustomerSupportService {

    private final SupportTicketRepository ticketRepository;
    private final TicketMessageRepository messageRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public SupportTicketEntity createTicket(Long customerId, String subject, String category, String priority, String initialMessage) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        String num = "TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SupportTicketEntity ticket = SupportTicketEntity.builder()
                .ticketNumber(num)
                .customer(customer)
                .subject(subject)
                .category(category)
                .priority(priority)
                .status("OPEN")
                .build();

        SupportTicketEntity savedTicket = ticketRepository.save(ticket);

        TicketMessageEntity msg = TicketMessageEntity.builder()
                .ticket(savedTicket)
                .senderName(customer.getFirstName() + " " + customer.getLastName())
                .senderRole("CUSTOMER")
                .messageText(initialMessage)
                .build();

        messageRepository.save(msg);

        auditService.logAction("CREATE_SUPPORT_TICKET", customer.getUser().getUsername(), "SUPPORT_TICKET", savedTicket.getTicketId().toString(), null, "Created Ticket: " + num);
        return savedTicket;
    }

    @Override
    @Transactional
    public TicketMessageEntity replyToTicket(Long ticketId, String senderName, String senderRole, String messageText) {
        SupportTicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        ticket.setStatus("IN_PROGRESS");
        ticketRepository.save(ticket);

        TicketMessageEntity msg = TicketMessageEntity.builder()
                .ticket(ticket)
                .senderName(senderName)
                .senderRole(senderRole)
                .messageText(messageText)
                .build();

        return messageRepository.save(msg);
    }

    @Override
    @Transactional
    public SupportTicketEntity resolveTicket(Long ticketId) {
        SupportTicketEntity ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("SupportTicket", "id", ticketId));

        ticket.setStatus("RESOLVED");
        return ticketRepository.save(ticket);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SupportTicketEntity> getCustomerTickets(Long customerId) {
        return ticketRepository.findByCustomer_CustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketMessageEntity> getTicketMessages(Long ticketId) {
        return messageRepository.findByTicket_TicketIdOrderBySentAtAsc(ticketId);
    }
}
