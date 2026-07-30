package com.bank.enterprise.exception;

public class LoanEligibilityException extends BankException {
    public LoanEligibilityException(String reason) {
        super("Loan application rejected due to eligibility check failure: " + reason);
    }
}
