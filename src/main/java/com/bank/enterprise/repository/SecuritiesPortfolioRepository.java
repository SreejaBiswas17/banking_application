package com.bank.enterprise.repository;

import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SecuritiesPortfolioRepository extends JpaRepository<SecuritiesPortfolioEntity, Long> {
    Optional<SecuritiesPortfolioEntity> findByCustomer_CustomerId(Long customerId);
}
