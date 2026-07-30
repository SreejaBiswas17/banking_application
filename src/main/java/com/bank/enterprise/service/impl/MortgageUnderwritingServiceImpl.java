package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.model.MortgagePropertyEntity;
import com.bank.enterprise.repository.LoanRepository;
import com.bank.enterprise.repository.MortgagePropertyRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.MortgageUnderwritingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class MortgageUnderwritingServiceImpl implements MortgageUnderwritingService {

    private final MortgagePropertyRepository mortgageRepository;
    private final LoanRepository loanRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public MortgagePropertyEntity registerMortgageProperty(Long loanId, String propertyAddress, BigDecimal appraisedValue, String propertyType) {
        LoanEntity loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("Loan", "id", loanId));

        BigDecimal ltv = loan.getPrincipalAmount().divide(appraisedValue, 4, RoundingMode.HALF_UP);

        MortgagePropertyEntity property = MortgagePropertyEntity.builder()
                .loan(loan)
                .propertyAddress(propertyAddress)
                .appraisedValue(appraisedValue)
                .ltvRatio(ltv)
                .propertyType(propertyType)
                .build();

        MortgagePropertyEntity saved = mortgageRepository.save(property);
        auditService.logAction("REGISTER_MORTGAGE_PROPERTY", "MORTGAGE_UNDERWRITER", "MORTGAGE_PROPERTY", saved.getPropertyId().toString(), null, "Registered property, LTV: " + ltv);
        return saved;
    }
}
