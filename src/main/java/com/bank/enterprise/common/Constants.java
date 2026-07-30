package com.bank.enterprise.common;

import java.math.BigDecimal;

public final class Constants {

    private Constants() {}

    public static final String SYSTEM_USER = "SYSTEM_AUTOMATION";
    public static final BigDecimal DEFAULT_INTEREST_RATE_SAVINGS = new BigDecimal("0.0350"); // 3.5%
    public static final BigDecimal DEFAULT_INTEREST_RATE_FIXED = new BigDecimal("0.0650"); // 6.5%
    public static final BigDecimal MAX_DAILY_TRANSFER_LIMIT = new BigDecimal("500000.00");
    public static final BigDecimal MIN_ACCOUNT_BALANCE_SAVINGS = new BigDecimal("100.00");
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
