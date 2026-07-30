package com.bank.enterprise.reporting.generator;

import com.bank.enterprise.dto.AccountDto;
import com.bank.enterprise.dto.TransactionDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
public class CustomerStatementCsvGenerator {

    public String generateCsvStatement(AccountDto.AccountResponse account, List<TransactionDto.TransactionResponse> transactions, LocalDate startDate, LocalDate endDate) {
        StringBuilder csv = new StringBuilder();
        csv.append("Account Number,Customer Name,Account Type,Currency,Balance\n");
        csv.append(String.format("%s,%s,%s,%s,%s\n\n",
                account.getAccountNumber(),
                account.getCustomerFullName(),
                account.getAccountType(),
                account.getCurrency(),
                account.getBalance()));

        csv.append("Statement Period: ").append(startDate).append(" to ").append(endDate).append("\n\n");
        csv.append("Transaction Ref,Type,Source Acc,Dest Acc,Amount,Fee,Status,Initiated At\n");

        for (TransactionDto.TransactionResponse tx : transactions) {
            csv.append(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                    tx.getTransactionReference(),
                    tx.getTransactionType(),
                    tx.getSourceAccountNumber() != null ? tx.getSourceAccountNumber() : "N/A",
                    tx.getDestinationAccountNumber() != null ? tx.getDestinationAccountNumber() : "N/A",
                    tx.getAmount(),
                    tx.getFeeAmount(),
                    tx.getStatus(),
                    tx.getInitiatedAt()));
        }

        return csv.toString();
    }
}
