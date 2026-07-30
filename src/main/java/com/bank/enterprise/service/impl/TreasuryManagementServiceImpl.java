package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.MoneyMarketDealEntity;
import com.bank.enterprise.model.TreasuryBondEntity;
import com.bank.enterprise.repository.MoneyMarketRepository;
import com.bank.enterprise.repository.TreasuryBondRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.TreasuryManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TreasuryManagementServiceImpl implements TreasuryManagementService {

    private final TreasuryBondRepository bondRepository;
    private final MoneyMarketRepository dealRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public TreasuryBondEntity purchaseBond(String isin, String issuerName, BigDecimal faceValue, BigDecimal couponRate, Currency currency, LocalDate maturityDate, String holdingType) {
        TreasuryBondEntity bond = TreasuryBondEntity.builder()
                .isin(isin)
                .issuerName(issuerName)
                .faceValue(faceValue)
                .couponRate(couponRate)
                .currency(currency)
                .maturityDate(maturityDate)
                .holdingType(holdingType)
                .build();

        TreasuryBondEntity saved = bondRepository.save(bond);
        auditService.logAction("PURCHASE_TREASURY_BOND", "TREASURY_DESK", "TREASURY_BOND", saved.getBondId().toString(), null, "Purchased Bond ISIN: " + isin);
        return saved;
    }

    @Override
    @Transactional
    public MoneyMarketDealEntity executeInterbankDeal(String counterpartyBank, String dealType, BigDecimal principalAmount, BigDecimal interestRate, Currency currency, LocalDate startDate, LocalDate maturityDate) {
        String ref = "MM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        MoneyMarketDealEntity deal = MoneyMarketDealEntity.builder()
                .dealReference(ref)
                .counterpartyBank(counterpartyBank)
                .dealType(dealType)
                .principalAmount(principalAmount)
                .interestRate(interestRate)
                .currency(currency)
                .startDate(startDate)
                .maturityDate(maturityDate)
                .build();

        MoneyMarketDealEntity saved = dealRepository.save(deal);
        auditService.logAction("EXECUTE_MONEY_MARKET_DEAL", "TREASURY_DESK", "MONEY_MARKET_DEAL", saved.getDealId().toString(), null, "Executed Deal Ref: " + ref);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TreasuryBondEntity> getAllBonds() {
        return bondRepository.findAll();
    }
}
