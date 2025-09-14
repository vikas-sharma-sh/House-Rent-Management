package com.management.houserent.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String jwtSecretBase64;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    private SecretKey key() {
        byte[] raw = Decoders.BASE64.decode(jwtSecretBase64);
        return Keys.hmacShaKeyFor(raw);
    }

    public String generateToken(String subject, String role) {
        Map<String, Object> claims = new HashMap<>();
        // Always include role claim so filter can avoid DB lookup
        claims.put("role", role.startsWith("ROLE_") ? role : "ROLE_" + role);
        return generateToken(subject, claims);
    }

    public String generateToken(String subject, Map<String, Object> extraClaims) {
        if (extraClaims == null) extraClaims = new HashMap<>();
        Instant now = Instant.now();
        return Jwts.builder()
                .claims(extraClaims)
                .subject(subject)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(jwtExpirationMs)))
                .signWith(key(), Jwts.SIG.HS256)  // ✅ 0.13.x API
                .compact();
    }

    public Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new IllegalArgumentException("Invalid JWT token", e);
        }
    }

    public <T> T extractClaim(String token, java.util.function.Function<Claims, T> resolver) {
        return resolver.apply(extractAllClaims(token));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
        return extractClaim(token, c -> c.get("role", String.class));
    }

    public boolean isTokenValid(String token, String username) {
        Claims claims = extractAllClaims(token);
        String subject = claims.getSubject();
        Date exp = claims.getExpiration();
        return subject != null && subject.equals(username)
                && exp != null && exp.after(new Date());
    }
}
