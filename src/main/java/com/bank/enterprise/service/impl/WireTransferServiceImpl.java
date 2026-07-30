package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.FedWirePaymentEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.FedWireRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.TransactionService;
import com.bank.enterprise.service.WireTransferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WireTransferServiceImpl implements WireTransferService {

    private final FedWireRepository fedWireRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public FedWirePaymentEntity executeFedWireTransfer(Long senderAccountId, String routingNumber, String accountNumber, String beneficiaryName, BigDecimal amount, Currency currency) {
        AccountEntity sender = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "id", senderAccountId));

        if (sender.getAvailableBalance().compareTo(amount) < 0) {
            throw new BankException("Insufficient funds for FedWire settlement");
        }

        // Deduct wire amount
        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(sender.getAccountNumber())
                .destinationAccountNumber("BANK_FEDWIRE_CLEARING_POOL")
                .amount(amount)
                .description("FedWire Outward Transfer to " + beneficiaryName)
                .build());

        String imad = "20260730FEDW" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String omad = "20260730OMAD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        FedWirePaymentEntity wire = FedWirePaymentEntity.builder()
                .imadNumber(imad)
                .omadNumber(omad)
                .senderAccount(sender)
                .beneficiaryRoutingNumber(routingNumber)
                .beneficiaryAccountNumber(accountNumber)
                .beneficiaryName(beneficiaryName)
                .amount(amount)
                .currency(currency)
                .status("CLEARED")
                .build();

        FedWirePaymentEntity saved = fedWireRepository.save(wire);
        auditService.logAction("EXECUTE_FEDWIRE", sender.getCustomer().getUser().getUsername(), "FEDWIRE_PAYMENT", saved.getWireId().toString(), null, "IMAD: " + imad);
        return saved;
    }
}
