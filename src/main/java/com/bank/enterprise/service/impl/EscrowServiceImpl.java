package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.Currency;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.CustomerEntity;
import com.bank.enterprise.model.EscrowAgreementEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.CustomerRepository;
import com.bank.enterprise.repository.EscrowAgreementRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.EscrowService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EscrowServiceImpl implements EscrowService {

    private final EscrowAgreementRepository escrowRepository;
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    @Override
    @Transactional
    public EscrowAgreementEntity setupEscrowAgreement(Long buyerCustomerId, Long sellerCustomerId, Long escrowAccountId, BigDecimal totalAmount, Currency currency) {
        CustomerEntity buyer = customerRepository.findById(buyerCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Buyer Customer", "id", buyerCustomerId));

        CustomerEntity seller = customerRepository.findById(sellerCustomerId)
                .orElseThrow(() -> new ResourceNotFoundException("Seller Customer", "id", sellerCustomerId));

        AccountEntity escrowAcc = accountRepository.findById(escrowAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Escrow Account", "id", escrowAccountId));

        String num = "ESC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        EscrowAgreementEntity agreement = EscrowAgreementEntity.builder()
                .escrowNumber(num)
                .buyerCustomer(buyer)
                .sellerCustomer(seller)
                .escrowAccount(escrowAcc)
                .totalEscrowAmount(totalAmount)
                .releasedAmount(BigDecimal.ZERO)
                .currency(currency)
                .status("FUNDED")
                .build();

        EscrowAgreementEntity saved = escrowRepository.save(agreement);
        auditService.logAction("SETUP_ESCROW_AGREEMENT", buyer.getUser().getUsername(), "ESCROW_AGREEMENT", saved.getEscrowId().toString(), null, "Escrow: " + num);
        return saved;
    }

    @Override
    @Transactional
    public void releaseEscrowFunds(Long escrowId, BigDecimal releaseAmount, String sellerAccountNumber) {
        EscrowAgreementEntity escrow = escrowRepository.findById(escrowId)
                .orElseThrow(() -> new ResourceNotFoundException("EscrowAgreement", "id", escrowId));

        BigDecimal remaining = escrow.getTotalEscrowAmount().subtract(escrow.getReleasedAmount());
        if (remaining.compareTo(releaseAmount) < 0) {
            throw new BankException("Release amount exceeds remaining unreleased escrow balance");
        }

        transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                .sourceAccountNumber(escrow.getEscrowAccount().getAccountNumber())
                .destinationAccountNumber(sellerAccountNumber)
                .amount(releaseAmount)
                .description("Escrow Milestone Payout")
                .build());

        escrow.setReleasedAmount(escrow.getReleasedAmount().add(releaseAmount));
        if (escrow.getReleasedAmount().compareTo(escrow.getTotalEscrowAmount()) == 0) {
            escrow.setStatus("COMPLETED");
        } else {
            escrow.setStatus("MILESTONE_RELEASE");
        }

        escrowRepository.save(escrow);
        auditService.logAction("RELEASE_ESCROW_FUNDS", "ESCROW_AGENT", "ESCROW_AGREEMENT", escrowId.toString(), null, "Released: " + releaseAmount);
    }
}
