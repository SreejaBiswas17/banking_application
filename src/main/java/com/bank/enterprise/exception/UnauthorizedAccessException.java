package com.bank.enterprise.exception;

public class UnauthorizedAccessException extends BankException {
    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
