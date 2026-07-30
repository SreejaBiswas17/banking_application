package com.bank.enterprise.service;

import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;

import java.math.BigDecimal;
import java.util.List;

public interface InvestmentService {
    MutualFundHoldingEntity buyMutualFundUnits(Long customerId, Long accountId, String schemeCode, BigDecimal investmentAmount);
    List<MutualFundHoldingEntity> getCustomerPortfolio(Long customerId);
    List<MutualFundSchemeEntity> getAvailableSchemes();
}
