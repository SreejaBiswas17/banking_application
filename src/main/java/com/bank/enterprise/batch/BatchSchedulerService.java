package com.bank.enterprise.batch;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.EmiStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.EmiScheduleEntity;
import com.bank.enterprise.model.StandingInstructionEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.EmiScheduleRepository;
import com.bank.enterprise.repository.StandingInstructionRepository;
import com.bank.enterprise.service.AccountService;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchSchedulerService {

    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final StandingInstructionRepository standingInstructionRepository;
    private final TransactionService transactionService;
    private final AuditService auditService;

    /**
     * Runs at 00:00:00 on the first day of every month to calculate savings interest.
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void executeMonthlyInterestJob() {
        accountService.applyInterestToSavingsAccounts();
        auditService.logAction("BATCH_INTEREST_JOB", "CRON_SCHEDULER", "SYSTEM", "BATCH_01", null, "Completed Monthly Savings Interest Calculation");
    }

    /**
     * Runs daily at 01:00:00 AM to mark accounts dormant if inactive for > 365 days.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void executeDormantAccountDetectionJob() {
        List<AccountEntity> activeAccounts = accountRepository.findAll();
        for (AccountEntity account : activeAccounts) {
            if (account.getAccountStatus() == AccountStatus.ACTIVE && account.getUpdatedAt().isBefore(LocalDate.now().minusDays(365).atStartOfDay())) {
                account.setAccountStatus(AccountStatus.DORMANT);
                accountRepository.save(account);
                auditService.logAction("ACCOUNT_MARKED_DORMANT", "CRON_SCHEDULER", "ACCOUNT", account.getAccountId().toString(), "ACTIVE", "DORMANT");
            }
        }
    }

    /**
     * Runs daily at 02:00:00 AM to flag overdue EMI installments.
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void executeOverdueEmiDetectionJob() {
        List<EmiScheduleEntity> overdueEmis = emiScheduleRepository.findOverdueEmis(LocalDate.now());
        for (EmiScheduleEntity emi : overdueEmis) {
            emi.setStatus(EmiStatus.OVERDUE);
            emiScheduleRepository.save(emi);
            auditService.logAction("EMI_MARKED_OVERDUE", "CRON_SCHEDULER", "EMI_SCHEDULE", emi.getScheduleId().toString(), "UNPAID", "OVERDUE");
        }
    }

    /**
     * Runs daily at 03:00:00 AM to execute Standing Instructions.
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void executeStandingInstructionsJob() {
        int dayOfMonth = LocalDate.now().getDayOfMonth();
        List<StandingInstructionEntity> instructions = standingInstructionRepository.findByIsActiveTrueAndExecutionDayOfMonth(dayOfMonth);
        for (StandingInstructionEntity si : instructions) {
            try {
                transactionService.transferFunds(TransactionDto.TransferRequest.builder()
                        .sourceAccountNumber(si.getSourceAccount().getAccountNumber())
                        .destinationAccountNumber(si.getDestinationAccount().getAccountNumber())
                        .amount(si.getAmount())
                        .transferType(si.getTransactionType() != null ? si.getTransactionType() : TransactionType.INTERNAL_TRANSFER)
                        .description("Automated Standing Instruction #" + si.getInstructionId())
                        .build());
                si.setLastExecutionDate(LocalDate.now());
                standingInstructionRepository.save(si);
            } catch (Exception e) {
                auditService.logAction("STANDING_INSTRUCTION_FAILED", "CRON_SCHEDULER", "STANDING_INSTRUCTION", si.getInstructionId().toString(), null, "Failed: " + e.getMessage());
            }
        }
    }
}
