package com.bank.enterprise.service;

import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.PayrollBatchEntity;

import java.math.BigDecimal;
import java.util.List;

public interface CorporateBankingService {
    CorporateAccountEntity registerCorporateAccount(String companyName, String regNum, String taxId, Long primaryAccountId, BigDecimal creditLimit);
    PayrollBatchEntity executePayrollBatch(Long corporateId, int totalEmployees, BigDecimal totalAmount);
    List<PayrollBatchEntity> getCorporatePayrollHistory(Long corporateId);
}
