package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.common.KycStatus;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.LetterOfCreditEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.LetterOfCreditRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.TradeFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TradeFinanceServiceImpl implements TradeFinanceService {

    private final LetterOfCreditRepository lcRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public LetterOfCreditEntity issueLetterOfCredit(Long customerId, String beneficiaryName, String advisingBankSwift, BigDecimal amount, Currency currency, LocalDate expiryDate) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        if (customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new BankException("Cannot issue Letter of Credit. Customer KYC status is " + customer.getKycStatus());
        }

        String lcNum = "LC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LetterOfCreditEntity lc = LetterOfCreditEntity.builder()
                .lcNumber(lcNum)
                .applicantCustomer(customer)
                .beneficiaryName(beneficiaryName)
                .advisingBankSwift(advisingBankSwift)
                .amount(amount)
                .currency(currency)
                .expiryDate(expiryDate)
                .status("ISSUED")
                .build();

        LetterOfCreditEntity savedLc = lcRepository.save(lc);
        auditService.logAction("ISSUE_LETTER_OF_CREDIT", customer.getUser().getUsername(), "LETTER_OF_CREDIT", savedLc.getLcId().toString(), null, "Issued LC: " + lcNum);
        return savedLc;
    }

    @Override
    @Transactional(readOnly = true)
    public LetterOfCreditEntity getLcByNumber(String lcNumber) {
        return lcRepository.findByLcNumber(lcNumber)
                .orElseThrow(() -> new ResourceNotFoundException("LetterOfCredit", "lcNumber", lcNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<LetterOfCreditEntity> getCustomerLcs(Long customerId) {
        return lcRepository.findByApplicantCustomer_CustomerId(customerId);
    }

    @Override
    @Transactional
    public void dischargeLc(Long lcId) {
        LetterOfCreditEntity lc = lcRepository.findById(lcId)
                .orElseThrow(() -> new ResourceNotFoundException("LetterOfCredit", "id", lcId));

        lc.setStatus("DISCHARGED");
        lcRepository.save(lc);

        auditService.logAction("DISCHARGE_LETTER_OF_CREDIT", "SYSTEM", "LETTER_OF_CREDIT", lcId.toString(), "ISSUED", "DISCHARGED");
    }
}
