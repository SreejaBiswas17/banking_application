package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.LetterOfCreditEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TradeFinanceService {
    LetterOfCreditEntity issueLetterOfCredit(Long customerId, String beneficiaryName, String advisingBankSwift, BigDecimal amount, Currency currency, LocalDate expiryDate);
    LetterOfCreditEntity getLcByNumber(String lcNumber);
    List<LetterOfCreditEntity> getCustomerLcs(Long customerId);
    void dischargeLc(Long lcId);
}
