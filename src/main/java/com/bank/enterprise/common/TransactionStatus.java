package com.bank.enterprise.common;

public enum TransactionStatus {
    INITIATED,
    PENDING_VERIFICATION,
    COMPLETED,
    FAILED,
    REVERSED,
    CANCELLED,
    FLAGGED_FOR_AUDIT
}
