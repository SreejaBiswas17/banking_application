package com.bank.enterprise.service.impl;

import com.bank.enterprise.common.AccountStatus;
import com.bank.enterprise.common.Constants;
import com.bank.enterprise.common.TransactionStatus;
import com.bank.enterprise.common.TransactionType;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.exception.BankException;
import com.bank.enterprise.exception.InsufficientBalanceException;
import com.bank.enterprise.exception.InvalidTransactionException;
import com.bank.enterprise.exception.ResourceNotFoundException;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.TransactionEntity;
import com.bank.enterprise.repository.AccountRepository;
import com.bank.enterprise.repository.TransactionRepository;
import com.bank.enterprise.service.AuditService;
import com.bank.enterprise.service.NotificationService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public TransactionDto.TransactionResponse deposit(TransactionDto.DepositRequest request) {
        AccountEntity account = accountRepository.findByAccountNumberWithLock(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", request.getAccountNumber()));

        if (account.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException("Deposit failed. Account status is " + account.getAccountStatus());
        }

        BigDecimal newBalance = account.getBalance().add(request.getAmount());
        account.setBalance(newBalance);
        account.setAvailableBalance(newBalance);
        accountRepository.save(account);

        String reference = "DEP-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionReference(reference)
                .destinationAccount(account)
                .transactionType(TransactionType.DEPOSIT)
                .amount(request.getAmount())
                .feeAmount(BigDecimal.ZERO)
                .currency(account.getCurrency())
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription() != null ? request.getDescription() : "Cash Deposit")
                .completedAt(LocalDateTime.now())
                .build();

        TransactionEntity savedTx = transactionRepository.save(transaction);

        auditService.logAction("ACCOUNT_DEPOSIT", "SYSTEM", "TRANSACTION", savedTx.getTransactionId().toString(), null, "Deposited " + request.getAmount() + " to " + account.getAccountNumber());
        notificationService.sendNotification(account.getCustomer().getUser().getUserId(), "DEPOSIT_SUCCESS", "Deposit of " + request.getAmount() + " credited to account " + account.getAccountNumber());

        return mapToTransactionResponse(savedTx);
    }

    @Override
    @Transactional
    public TransactionDto.TransactionResponse transferFunds(TransactionDto.TransferRequest request) {
        if (request.getSourceAccountNumber().equals(request.getDestinationAccountNumber())) {
            throw new InvalidTransactionException("Source and destination accounts cannot be identical");
        }

        AccountEntity sourceAccount = accountRepository.findByAccountNumberWithLock(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Source Account", "accountNumber", request.getSourceAccountNumber()));

        AccountEntity destAccount = accountRepository.findByAccountNumberWithLock(request.getDestinationAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("Destination Account", "accountNumber", request.getDestinationAccountNumber()));

        if (sourceAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException("Transfer failed. Source account status is " + sourceAccount.getAccountStatus());
        }

        if (destAccount.getAccountStatus() != AccountStatus.ACTIVE) {
            throw new InvalidTransactionException("Transfer failed. Destination account status is " + destAccount.getAccountStatus());
        }

        // Daily Limit Check
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        BigDecimal dailySum = transactionRepository.calculateDailyTransferSum(sourceAccount.getAccountId(), request.getTransferType() != null ? request.getTransferType() : TransactionType.INTERNAL_TRANSFER, startOfDay);
        if (dailySum.add(request.getAmount()).compareTo(Constants.MAX_DAILY_TRANSFER_LIMIT) > 0) {
            throw new InvalidTransactionException("Daily transfer limit exceeded. Current daily transfers: " + dailySum);
        }

        // Fee Calculation
        BigDecimal feeAmount = BigDecimal.ZERO;
        TransactionType type = request.getTransferType() != null ? request.getTransferType() : TransactionType.INTERNAL_TRANSFER;
        if (type == TransactionType.NEFT_TRANSFER || type == TransactionType.RTGS_TRANSFER) {
            feeAmount = request.getAmount().multiply(new BigDecimal("0.001")).setScale(2, RoundingMode.HALF_UP); // 0.1% fee
        }

        BigDecimal totalDeduction = request.getAmount().add(feeAmount);
        BigDecimal effectiveAvailable = sourceAccount.getAvailableBalance().add(sourceAccount.getOverdraftLimit());

        if (effectiveAvailable.compareTo(totalDeduction) < 0) {
            throw new InsufficientBalanceException(sourceAccount.getAccountNumber(), totalDeduction, sourceAccount.getAvailableBalance());
        }

        // Execute Balance Shift
        sourceAccount.setBalance(sourceAccount.getBalance().subtract(totalDeduction));
        sourceAccount.setAvailableBalance(sourceAccount.getAvailableBalance().subtract(totalDeduction));

        destAccount.setBalance(destAccount.getBalance().add(request.getAmount()));
        destAccount.setAvailableBalance(destAccount.getAvailableBalance().add(request.getAmount()));

        accountRepository.save(sourceAccount);
        accountRepository.save(destAccount);

        String reference = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        TransactionEntity transaction = TransactionEntity.builder()
                .transactionReference(reference)
                .sourceAccount(sourceAccount)
                .destinationAccount(destAccount)
                .transactionType(type)
                .amount(request.getAmount())
                .feeAmount(feeAmount)
                .currency(sourceAccount.getCurrency())
                .status(TransactionStatus.COMPLETED)
                .description(request.getDescription() != null ? request.getDescription() : "Funds Transfer")
                .completedAt(LocalDateTime.now())
                .build();

        TransactionEntity savedTx = transactionRepository.save(transaction);

        auditService.logAction("FUNDS_TRANSFER", sourceAccount.getCustomer().getUser().getUsername(), "TRANSACTION", savedTx.getTransactionId().toString(), null, "Transferred " + request.getAmount() + " to " + destAccount.getAccountNumber());
        notificationService.sendNotification(sourceAccount.getCustomer().getUser().getUserId(), "DEBIT_ALERT", "Transferred " + request.getAmount() + " to " + destAccount.getAccountNumber());
        notificationService.sendNotification(destAccount.getCustomer().getUser().getUserId(), "CREDIT_ALERT", "Received " + request.getAmount() + " from " + sourceAccount.getAccountNumber());

        return mapToTransactionResponse(savedTx);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionDto.TransactionResponse getTransactionByReference(String reference) {
        TransactionEntity tx = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "reference", reference));
        return mapToTransactionResponse(tx);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TransactionDto.TransactionResponse> getAccountStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "accountNumber", accountNumber));

        LocalDateTime start = startDate != null ? startDate : LocalDateTime.now().minusDays(30);
        LocalDateTime end = endDate != null ? endDate : LocalDateTime.now();

        return transactionRepository.findAccountStatement(account.getAccountId(), start, end, pageable)
                .map(this::mapToTransactionResponse);
    }

    @Override
    @Transactional
    public TransactionDto.TransactionResponse reverseTransaction(String reference, String reason) {
        TransactionEntity tx = transactionRepository.findByTransactionReference(reference)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "reference", reference));

        if (tx.getStatus() != TransactionStatus.COMPLETED) {
            throw new InvalidTransactionException("Cannot reverse transaction in status " + tx.getStatus());
        }

        AccountEntity source = tx.getSourceAccount();
        AccountEntity dest = tx.getDestinationAccount();

        if (dest.getAvailableBalance().compareTo(tx.getAmount()) < 0) {
            throw new BankException("Destination account has insufficient funds to process reversal");
        }

        dest.setBalance(dest.getBalance().subtract(tx.getAmount()));
        dest.setAvailableBalance(dest.getAvailableBalance().subtract(tx.getAmount()));

        source.setBalance(source.getBalance().add(tx.getAmount()));
        source.setAvailableBalance(source.getAvailableBalance().add(tx.getAmount()));

        accountRepository.save(dest);
        accountRepository.save(source);

        tx.setStatus(TransactionStatus.REVERSED);
        tx.setFailureReason("Reversed: " + reason);
        TransactionEntity updated = transactionRepository.save(tx);

        auditService.logAction("TRANSACTION_REVERSAL", "ADMIN", "TRANSACTION", tx.getTransactionId().toString(), "COMPLETED", "REVERSED: " + reason);

        return mapToTransactionResponse(updated);
    }

    private TransactionDto.TransactionResponse mapToTransactionResponse(TransactionEntity entity) {
        return TransactionDto.TransactionResponse.builder()
                .transactionId(entity.getTransactionId())
                .transactionReference(entity.getTransactionReference())
                .sourceAccountNumber(entity.getSourceAccount() != null ? entity.getSourceAccount().getAccountNumber() : null)
                .destinationAccountNumber(entity.getDestinationAccount() != null ? entity.getDestinationAccount().getAccountNumber() : null)
                .transactionType(entity.getTransactionType())
                .amount(entity.getAmount())
                .feeAmount(entity.getFeeAmount())
                .currency(entity.getCurrency())
                .status(entity.getStatus())
                .description(entity.getDescription())
                .failureReason(entity.getFailureReason())
                .initiatedAt(entity.getInitiatedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}
