package com.bank.enterprise.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public final class EncryptionUtils {

    private EncryptionUtils() {}

    public static String hashSHA256(String input) {
        if (input == null) return null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 Algorithm not available", e);
        }
    }

    public static String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 12) {
            return "****";
        }
        String clean = cardNumber.replaceAll("\\s+", "");
        return clean.substring(0, 4) + "-XXXX-XXXX-" + clean.substring(clean.length() - 4);
    }

    public static String maskNationalId(String nationalId) {
        if (nationalId == null || nationalId.length() < 4) {
            return "***";
        }
        return "***-**-" + nationalId.substring(nationalId.length() - 4);
    }
}
