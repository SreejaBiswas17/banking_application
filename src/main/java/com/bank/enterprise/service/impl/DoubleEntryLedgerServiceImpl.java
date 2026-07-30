package com.bank.enterprise.service.impl;

import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.GeneralLedgerAccountEntity;
import com.bank.enterprise.model.JournalEntryEntity;
import com.bank.enterprise.model.JournalLineItemEntity;
import com.bank.enterprise.repository.GeneralLedgerRepository;
import com.bank.enterprise.repository.JournalEntryRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.DoubleEntryLedgerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DoubleEntryLedgerServiceImpl implements DoubleEntryLedgerService {

    private final GeneralLedgerRepository glRepository;
    private final JournalEntryRepository journalRepository;
    private final AuditService auditService;

    @Override
    @Transactional
    public JournalEntryEntity postJournalEntry(String debitGlCode, String creditGlCode, BigDecimal amount, String narration) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BankException("Journal entry amount must be positive");
        }

        GeneralLedgerAccountEntity debitGl = glRepository.findByGlCode(debitGlCode)
                .orElseThrow(() -> new ResourceNotFoundException("GeneralLedgerAccount", "glCode", debitGlCode));

        GeneralLedgerAccountEntity creditGl = glRepository.findByGlCode(creditGlCode)
                .orElseThrow(() -> new ResourceNotFoundException("GeneralLedgerAccount", "glCode", creditGlCode));

        // Update GL Balances
        debitGl.setCurrentBalance(debitGl.getCurrentBalance().add(amount));
        creditGl.setCurrentBalance(creditGl.getCurrentBalance().add(amount));
        glRepository.save(debitGl);
        glRepository.save(creditGl);

        String jnum = "JRN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        JournalEntryEntity journal = JournalEntryEntity.builder()
                .journalNumber(jnum)
                .narration(narration)
                .totalDebit(amount)
                .totalCredit(amount)
                .build();

        JournalLineItemEntity debitLine = JournalLineItemEntity.builder()
                .journalEntry(journal)
                .glAccount(debitGl)
                .debitAmount(amount)
                .creditAmount(BigDecimal.ZERO)
                .build();

        JournalLineItemEntity creditLine = JournalLineItemEntity.builder()
                .journalEntry(journal)
                .glAccount(creditGl)
                .debitAmount(BigDecimal.ZERO)
                .creditAmount(amount)
                .build();

        List<JournalLineItemEntity> lines = new ArrayList<>();
        lines.add(debitLine);
        lines.add(creditLine);
        journal.setLineItems(lines);

        JournalEntryEntity saved = journalRepository.save(journal);
        auditService.logAction("POST_JOURNAL_ENTRY", "ACCOUNTING_ENGINE", "JOURNAL_ENTRY", saved.getJournalId().toString(), null, "Posted Journal: " + jnum);
        return saved;
    }
}
