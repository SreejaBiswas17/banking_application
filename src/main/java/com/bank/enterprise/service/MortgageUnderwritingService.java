package com.bank.enterprise.service;

import com.bank.enterprise.model.MortgagePropertyEntity;

import java.math.BigDecimal;

public interface MortgageUnderwritingService {
    MortgagePropertyEntity registerMortgageProperty(Long loanId, String propertyAddress, BigDecimal appraisedValue, String propertyType);
}
