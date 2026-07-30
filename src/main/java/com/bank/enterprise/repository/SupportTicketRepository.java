package com.bank.enterprise.repository;

import com.bank.enterprise.model.DisputeClaimEntity;
import com.bank.enterprise.model.SupportTicketEntity;
import com.bank.enterprise.model.TicketMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository extends JpaRepository<SupportTicketEntity, Long> {
    Optional<SupportTicketEntity> findByTicketNumber(String ticketNumber);
    List<SupportTicketEntity> findByCustomer_CustomerId(Long customerId);
    List<SupportTicketEntity> findByStatus(String status);
}
