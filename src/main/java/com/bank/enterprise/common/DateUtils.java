package com.bank.enterprise.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtils {

    private DateUtils() {}

    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DateTimeFormatter.ofPattern(DEFAULT_DATE_FORMAT)) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DateTimeFormatter.ofPattern(DEFAULT_DATETIME_FORMAT)) : "";
    }

    public static boolean isAdult(LocalDate dateOfBirth) {
        if (dateOfBirth == null) return false;
        return dateOfBirth.plusYears(18).isBefore(LocalDate.now()) || dateOfBirth.plusYears(18).isEqual(LocalDate.now());
    }
}
