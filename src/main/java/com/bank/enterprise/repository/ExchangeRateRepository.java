package com.bank.enterprise.repository;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.ExchangeRateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {
    Optional<ExchangeRateEntity> findByBaseCurrencyAndTargetCurrency(Currency baseCurrency, Currency targetCurrency);
}
