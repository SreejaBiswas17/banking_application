package com.bank.enterprise.service;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.model.ParticipantBankEntity;
import com.bank.enterprise.model.SyndicatedLoanFacilityEntity;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface SyndicatedLoanService {
    SyndicatedLoanFacilityEntity createFacility(Long borrowerCorporateId, BigDecimal totalFacilityAmount, String leadArrangerBank, Currency currency, LocalDate maturityDate);
    ParticipantBankEntity addParticipantBank(Long facilityId, String bankName, String swiftBic, BigDecimal committedAmount);
}
