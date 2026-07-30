package com.bank.enterprise.service;

import com.bank.enterprise.model.JournalEntryEntity;

import java.math.BigDecimal;

public interface DoubleEntryLedgerService {
    JournalEntryEntity postJournalEntry(String debitGlCode, String creditGlCode, BigDecimal amount, String narration);
}
