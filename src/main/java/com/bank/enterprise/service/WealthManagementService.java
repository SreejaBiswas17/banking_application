package com.bank.enterprise.service;

import com.bank.enterprise.model.FixedDepositEntity;

import java.math.BigDecimal;
import java.util.List;

public interface WealthManagementService {
    FixedDepositEntity createFixedDeposit(Long customerId, Long accountId, BigDecimal amount, int tenureDays);
    List<FixedDepositEntity> getCustomerFixedDeposits(Long customerId);
    void processMaturedDeposits();
}
