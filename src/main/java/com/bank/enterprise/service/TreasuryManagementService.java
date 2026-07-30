package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.MoneyMarketDealEntity;
import com.bank.enterprise.model.TreasuryBondEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TreasuryManagementService {
    TreasuryBondEntity purchaseBond(String isin, String issuerName, BigDecimal faceValue, BigDecimal couponRate, Currency currency, LocalDate maturityDate, String holdingType);
    MoneyMarketDealEntity executeInterbankDeal(String counterpartyBank, String dealType, BigDecimal principalAmount, BigDecimal interestRate, Currency currency, LocalDate startDate, LocalDate maturityDate);
    List<TreasuryBondEntity> getAllBonds();
}
