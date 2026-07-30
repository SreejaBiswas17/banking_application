package com.bank.enterprise.exception;

public class AccountLockedException extends BankException {
    public AccountLockedException(String username) {
        super("Account is locked for user: " + username + ". Please contact administrator.");
    }
}
