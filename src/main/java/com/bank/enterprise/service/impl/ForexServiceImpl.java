package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.ForexContractEntity;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.ForexContractRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.ForexService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ForexServiceImpl implements ForexService {

    private final ForexContractRepository forexRepository;
    private final CustomerRepository customerRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public ForexContractEntity bookContract(Long customerId, Currency buyCurrency, Currency sellCurrency, BigDecimal buyAmount) {
        CustomerEntity customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        BigDecimal mockRate = new BigDecimal("1.0850"); // e.g. EUR/USD
        BigDecimal sellAmount = buyAmount.multiply(mockRate).setScale(4, RoundingMode.HALF_UP);

        String num = "FX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        ForexContractEntity contract = ForexContractEntity.builder()
                .contractNumber(num)
                .customer(customer)
                .buyCurrency(buyCurrency)
                .sellCurrency(sellCurrency)
                .buyAmount(buyAmount)
                .sellAmount(sellAmount)
                .appliedRate(mockRate)
                .valueDate(LocalDate.now().plusDays(2)) // T+2 Spot Settlement
                .status("BOOKED")
                .build();

        ForexContractEntity saved = forexRepository.save(contract);
        auditService.logAction("BOOK_FOREX_CONTRACT", customer.getUser().getUsername(), "FOREX_CONTRACT", saved.getContractId().toString(), null, "Booked FX: " + num);
        return saved;
    }

    @Override
    @Transactional(readOnly = true)
    public ForexContractEntity getContractByNumber(String contractNumber) {
        return forexRepository.findByContractNumber(contractNumber)
                .orElseThrow(() -> new ResourceNotFoundException("ForexContract", "contractNumber", contractNumber));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForexContractEntity> getCustomerContracts(Long customerId) {
        return forexRepository.findByCustomer_CustomerId(customerId);
    }
}
