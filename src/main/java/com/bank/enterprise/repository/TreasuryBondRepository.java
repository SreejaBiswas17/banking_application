package com.bank.enterprise.repository;

import com.bank.enterprise.model.MoneyMarketDealEntity;
import com.bank.enterprise.model.TreasuryBondEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TreasuryBondRepository extends JpaRepository<TreasuryBondEntity, Long> {
    Optional<TreasuryBondEntity> findByIsin(String isin);
}
