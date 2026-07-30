package com.bank.enterprise.service.impl;

import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.MutualFundHoldingEntity;
import com.bank.enterprise.model.MutualFundSchemeEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.MutualFundHoldingRepository;
import com.bank.enterprise.repository.MutualFundSchemeRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.InvestmentService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InvestmentServiceImpl implements InvestmentService {

    private final MutualFundSchemeRepository schemeRepository;
    private final MutualFundHoldingRepository holdingRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public MutualFundHoldingEntity buyMutualFundUnits(Long customerId, Long accountId, String schemeCode, BigDecimal investmentAmount) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", accountId));

        MutualFundSchemeEntity scheme = schemeRepository.findBySchemeCode(schemeCode)
                .orElseThrow(() -> new ResourceNotFoundException("MutualFundScheme", "schemeCode", schemeCode));

        if (account.getAvailableBalance().compareTo(investmentAmount) < 0) {
            throw new BankException("Insufficient funds for investment purchase");
        }

        // Deduct investment amount
        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(account.getAccountNumber())
                .destinationAccountNumber("BANK_INVESTMENT_POOL_ACC")
                .amount(investmentAmount)
                .description("Mutual Fund Purchase: " + scheme.getSchemeName())
                .build());

        BigDecimal unitsAllocated = investmentAmount.divide(scheme.getCurrentNav(), 4, RoundingMode.HALF_UP);

        Optional<MutualFundHoldingEntity> existingHolding = holdingRepository.findByCustomer_CustomerIdAndScheme_SchemeId(customerId, scheme.getSchemeId());

        MutualFundHoldingEntity holding;
        if (existingHolding.isPresent()) {
            holding = existingHolding.get();
            holding.setTotalUnits(holding.getTotalUnits().add(unitsAllocated));
            holding.setTotalInvestedAmount(holding.getTotalInvestedAmount().add(investmentAmount));
        } else {
            holding = MutualFundHoldingEntity.builder()
                    .customer(customer)
                    .scheme(scheme)
                    .totalUnits(unitsAllocated)
                    .averagePurchaseNav(scheme.getCurrentNav())
                    .totalInvestedAmount(investmentAmount)
                    .build();
        }

        MutualFundHoldingEntity saved = holdingRepository.save(holding);
        auditService.logAction("MUTUAL_FUND_PURCHASE", customer.getUser().getUsername(), "MUTUAL_FUND_HOLDING", saved.getHoldingId().toString(), null, "Purchased units: " + unitsAllocated);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MutualFundHoldingEntity> getCustomerPortfolio(Long customerId) {
        return holdingRepository.findByCustomer_CustomerId(customerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MutualFundSchemeEntity> getAvailableSchemes() {
        return schemeRepository.findAll();
    }
}
