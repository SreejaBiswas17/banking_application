package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.ForexContractEntity;

import java.math.BigDecimal;
import java.util.List;

public interface ForexService {
    ForexContractEntity bookContract(Long customerId, Currency buyCurrency, Currency sellCurrency, BigDecimal buyAmount);
    ForexContractEntity getContractByNumber(String contractNumber);
    List<ForexContractEntity> getCustomerContracts(Long customerId);
}
