package com.bank.enterprise.repository;

import com.bank.enterprise.model.MoneyMarketDealEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MoneyMarketRepository extends JpaRepository<MoneyMarketDealEntity, Long> {
    Optional<MoneyMarketDealEntity> findByDealReference(String dealReference);
}
