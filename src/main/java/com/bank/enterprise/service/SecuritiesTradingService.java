package com.bank.enterprise.service;

import com.bank.enterprise.model.SecuritiesPortfolioEntity;
import com.bank.enterprise.model.StockOrderEntity;

import java.math.BigDecimal;

public interface SecuritiesTradingService {
    SecuritiesPortfolioEntity openDematPortfolio(Long customerId);
    StockOrderEntity executeStockTrade(Long customerId, Long accountId, String tickerSymbol, String orderType, int quantity, BigDecimal price);
}
