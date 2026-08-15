package com.searchengine.simulator.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Generates and validates JWTs using JJWT 0.12.3.
 */
@Component
public class JwtTokenProvider {

    // Using a hardcoded 256-bit base64 key for simplicity in local dev (32 bytes).
    // In production, this should be injected from secure configuration.
    private final String jwtSecret = "z4Xh3T5rP9oQ2W1mV6yB8nF0aC7uE3xLz4Xh3T5rP9o=";
    
    // 24 hours expiration
    private final long jwtExpirationMs = 86400000;

    private SecretKey getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception ex) {
            // Log exceptions related to token parsing (expired, invalid signature, etc.)
            return false;
        }
    }
}
