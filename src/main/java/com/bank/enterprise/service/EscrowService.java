package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.EscrowAgreementEntity;

import java.math.BigDecimal;

public interface EscrowService {
    EscrowAgreementEntity setupEscrowAgreement(Long buyerCustomerId, Long sellerCustomerId, Long escrowAccountId, BigDecimal totalAmount, Currency currency);
    void releaseEscrowFunds(Long escrowId, BigDecimal releaseAmount, String sellerAccountNumber);
}
