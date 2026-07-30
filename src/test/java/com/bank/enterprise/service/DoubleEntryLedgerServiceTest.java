package com.bank.enterprise.service;

import com.bank.enterprise.model.GeneralLedgerAccountEntity;
import com.bank.enterprise.model.JournalEntryEntity;
import com.bank.enterprise.repository.GeneralLedgerRepository;
import com.bank.enterprise.repository.JournalEntryRepository;
import com.bank.enterprise.service.impl.DoubleEntryLedgerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoubleEntryLedgerServiceTest {

    @Mock
    private GeneralLedgerRepository glRepository;

    @Mock
    private JournalEntryRepository journalRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private DoubleEntryLedgerServiceImpl ledgerService;

    private GeneralLedgerAccountEntity cashGl;
    private GeneralLedgerAccountEntity depositGl;

    @BeforeEach
    void setUp() {
        cashGl = GeneralLedgerAccountEntity.builder().glId(1L).glCode("GL-1001").glName("Vault Cash").accountCategory("ASSET").currentBalance(new BigDecimal("100000.00")).build();
        depositGl = GeneralLedgerAccountEntity.builder().glId(2L).glCode("GL-2001").glName("Customer Savings").accountCategory("LIABILITY").currentBalance(new BigDecimal("100000.00")).build();
    }

    @Test
    @DisplayName("Should post balanced journal entry")
    void postJournalEntry_Success() {
        when(glRepository.findByGlCode("GL-1001")).thenReturn(Optional.of(cashGl));
        when(glRepository.findByGlCode("GL-2001")).thenReturn(Optional.of(depositGl));

        JournalEntryEntity journal = JournalEntryEntity.builder()
                .journalId(10L)
                .journalNumber("JRN-12345678")
                .totalDebit(new BigDecimal("500.00"))
                .totalCredit(new BigDecimal("500.00"))
                .build();

        when(journalRepository.save(any(JournalEntryEntity.class))).thenReturn(journal);

        JournalEntryEntity result = ledgerService.postJournalEntry("GL-1001", "GL-2001", new BigDecimal("500.00"), "Vault deposit balancing");

        assertThat(result).isNotNull();
        assertThat(result.getJournalNumber()).isEqualTo("JRN-12345678");
        verify(glRepository, times(2)).save(any());
    }
}
