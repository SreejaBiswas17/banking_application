package com.bank.enterprise.repository;

import com.bank.enterprise.model.TicketMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessageEntity, Long> {
    List<TicketMessageEntity> findByTicket_TicketIdOrderBySentAtAsc(Long ticketId);
}
