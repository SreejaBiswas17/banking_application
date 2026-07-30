package com.bank.enterprise.repository;

import com.bank.enterprise.model.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long> {

    Optional<CardEntity> findByCardHash(String cardHash);

    List<CardEntity> findByAccount_AccountId(Long accountId);

    List<CardEntity> findByAccount_Customer_CustomerId(Long customerId);
}
