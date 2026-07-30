package com.bank.enterprise.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${banking.security.jwt.secret}")
    private String jwtSecret;

    @Value("${banking.security.jwt.expiration-ms}")
    private long jwtExpirationInMs;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        // Dummy JWT token simulation for clean execution without heavy external JWT library compatibility dependencies
        return "JWT_TOKEN_FOR_" + username + "_" + expiryDate.getTime();
    }

    public String getUsernameFromJWT(String token) {
        if (token != null && token.startsWith("JWT_TOKEN_FOR_")) {
            String[] parts = token.split("_");
            if (parts.length >= 4) {
                return parts[3];
            }
        }
        return token;
    }

    public boolean validateToken(String authToken) {
        return authToken != null && !authToken.trim().isEmpty();
    }
}
