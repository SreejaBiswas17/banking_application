package com.bank.enterprise.exception;

import java.math.BigDecimal;

public class InsufficientBalanceException extends BankException {
    public InsufficientBalanceException(String accountNumber, BigDecimal requestedAmount, BigDecimal availableBalance) {
        super(String.format("Insufficient funds in account %s. Requested: %s, Available: %s",
                accountNumber, requestedAmount, availableBalance));
    }
}
