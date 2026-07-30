package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.FedWirePaymentEntity;

import java.math.BigDecimal;

public interface WireTransferService {
    FedWirePaymentEntity executeFedWireTransfer(Long senderAccountId, String routingNumber, String accountNumber, String beneficiaryName, BigDecimal amount, Currency currency);
}
