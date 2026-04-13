package com.skillstorm.auth_service.Sevices;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secret;

    public JwtService(@Value("${TOKEN_SECRET}") String secret) {
        this.secret = secret;
    }

    // Generate a signing key from the secret
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /*
     * Generate a JWT token with the given email, role, and userId.
     * The token will expire in 24 hours.
    */
    public String generateToken(String email, String role, Long userId){
        long now = System.currentTimeMillis();
        long expiration = now + (1000 * 60 * 60 * 24); // 24 hours

        return Jwts.builder()
            .setSubject(email)
            .claim("role", role)
            .claim("userId", userId)
            .setIssuedAt(new Date(now))
            .setExpiration(new Date(expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /*
     * Validate the given JWT token.
     * Returns true if the token is valid, false otherwise.
    */
    public boolean validateToken(String token){
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
                return true;
        } catch (Exception e) {
            return false;
        }
    }

    /*
     * Extract all claims from the given JWT token.
    */
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /*
     * The rest are all extractors for specific claims in the token, 
     * such as email, role, userId, and expiration.
    */
    public String extractEmail(String token){
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    public Long extractUserId(String token){
        return extractAllClaims(token).get("userId", Long.class);
    }

    public long extractExpiration(String token) {
        return extractAllClaims(token).getExpiration().getTime();
    }
}
