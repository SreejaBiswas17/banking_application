package com.bank.enterprise.service;

import com.bank.enterprise.dto.TransactionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface TransactionService {
    TransactionDto.TransactionResponse deposit(TransactionDto.DepositRequest request);
    TransactionDto.TransactionResponse transferFunds(TransactionDto.TransferRequest request);
    TransactionDto.TransactionResponse getTransactionByReference(String reference);
    Page<TransactionDto.TransactionResponse> getAccountStatement(String accountNumber, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    TransactionDto.TransactionResponse reverseTransaction(String reference, String reason);
}
