package com.bank.enterprise.service.impl;

import com.bank.enterprise.dto.ReportDto;
import com.bank.enterprise.dto.TransactionDto;
import com.bank.enterprise.model.AccountEntity;
import com.bank.enterprise.model.LoanEntity;
import com.bank.enterprise.repository.*;
import com.bank.enterprise.service.ReportService;
import com.bank.enterprise.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final LoanRepository loanRepository;
    private final CardRepository cardRepository;
    private final TransactionService transactionService;

    @Override
    @Transactional(readOnly = true)
    public ReportDto.FinancialSummaryDto generateFinancialSummary() {
        long totalCustomers = customerRepository.count();
        long totalAccounts = accountRepository.count();
        long activeCards = cardRepository.count();

        List<AccountEntity> accounts = accountRepository.findAll();
        BigDecimal totalDeposits = accounts.stream()
                .map(AccountEntity::getBalance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<LoanEntity> loans = loanRepository.findAll();
        BigDecimal totalLoanDisbursed = loans.stream()
                .map(LoanEntity::getPrincipalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOutstanding = loans.stream()
                .map(LoanEntity::getOutstandingPrincipal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return ReportDto.FinancialSummaryDto.builder()
                .totalCustomers(totalCustomers)
                .totalAccounts(totalAccounts)
                .totalBankDeposits(totalDeposits)
                .totalLoansDisbursed(totalLoanDisbursed)
                .totalOutstandingLoans(totalOutstanding)
                .activeCardsCount(activeCards)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ReportDto.AccountStatementDto generateAccountStatement(String accountNumber, LocalDate startDate, LocalDate endDate) {
        AccountEntity account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));

        List<TransactionDto.TransactionResponse> transactions = transactionService
                .getAccountStatement(accountNumber, startDate.atStartOfDay(), endDate.atTime(23, 59, 59), Pageable.unpaged())
                .getContent();

        BigDecimal totalDeposits = transactions.stream()
                .filter(t -> accountNumber.equals(t.getDestinationAccountNumber()))
                .map(TransactionDto.TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalWithdrawals = transactions.stream()
                .filter(t -> accountNumber.equals(t.getSourceAccountNumber()))
                .map(TransactionDto.TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String customerName = account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName();

        return ReportDto.AccountStatementDto.builder()
                .accountNumber(accountNumber)
                .customerName(customerName)
                .startDate(startDate)
                .endDate(endDate)
                .openingBalance(account.getBalance().subtract(totalDeposits).add(totalWithdrawals))
                .closingBalance(account.getBalance())
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .transactions(transactions)
                .build();
    }
}
