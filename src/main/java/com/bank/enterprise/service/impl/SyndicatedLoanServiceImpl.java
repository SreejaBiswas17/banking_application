package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CorporateAccountEntity;
import com.bank.enterprise.model.ParticipantBankEntity;
import com.bank.enterprise.model.SyndicatedLoanFacilityEntity;
import com.bank.enterprise.repository.CorporateAccountRepository;
import com.bank.enterprise.repository.ParticipantBankRepository;
import com.bank.enterprise.repository.SyndicatedLoanRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.SyndicatedLoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SyndicatedLoanServiceImpl implements SyndicatedLoanService {

    private final SyndicatedLoanRepository syndicatedRepository;
    private final ParticipantBankRepository participantRepository;
    private final CorporateAccountRepository corporateRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public SyndicatedLoanFacilityEntity createFacility(Long borrowerCorporateId, BigDecimal totalFacilityAmount, String leadArrangerBank, Currency currency, LocalDate maturityDate) {
        CorporateAccountEntity corporate = corporateRepository.findById(borrowerCorporateId)
                .orElseThrow(() -> new ResourceNotFoundException("CorporateAccount", "id", borrowerCorporateId));

        String facNum = "SYND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        SyndicatedLoanFacilityEntity facility = SyndicatedLoanFacilityEntity.builder()
                .facilityNumber(facNum)
                .borrowerCorporate(corporate)
                .totalFacilityAmount(totalFacilityAmount)
                .leadArrangerBank(leadArrangerBank)
                .currency(currency)
                .maturityDate(maturityDate)
                .status("SYNDICATING")
                .build();

        SyndicatedLoanFacilityEntity saved = syndicatedRepository.save(facility);
        auditService.logAction("CREATE_SYNDICATED_FACILITY", "INVESTMENT_DESK", "SYNDICATED_FACILITY", saved.getFacilityId().toString(), null, "Facility: " + facNum);
        return saved;
    }

    @Override
    @Transactional
    public ParticipantBankEntity addParticipantBank(Long facilityId, String bankName, String swiftBic, BigDecimal committedAmount) {
        SyndicatedLoanFacilityEntity facility = syndicatedRepository.findById(facilityId)
                .orElseThrow(() -> new ResourceNotFoundException("SyndicatedFacility", "id", facilityId));

        BigDecimal share = committedAmount.divide(facility.getTotalFacilityAmount(), 4, RoundingMode.HALF_UP);

        ParticipantBankEntity participant = ParticipantBankEntity.builder()
                .facility(facility)
                .bankName(bankName)
                .swiftBic(swiftBic)
                .committedAmount(committedAmount)
                .participationSharePercent(share)
                .build();

        return participantRepository.save(participant);
    }
}
