package com.bank.enterprise.repository;

import com.bank.enterprise.model.StockOrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockOrderRepository extends JpaRepository<StockOrderEntity, Long> {
    List<StockOrderEntity> findByPortfolio_PortfolioId(Long portfolioId);
}
