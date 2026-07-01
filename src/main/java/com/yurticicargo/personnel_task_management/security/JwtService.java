package com.yurticicargo.personnel_task_management.security;

import com.yurticicargo.personnel_task_management.entity.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET_KEY = "yurtici-kargo-personnel-task-management-secret-key-2026";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 8;

    public String generateToken(Employee employee) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(employee.getUsername())
                .claim("userId", employee.getId())
                .claim("role", employee.getRole().name())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public Long extractUserId(String token) {
        Object userId = extractAllClaims(token).get("userId");

        if (userId instanceof Integer) {
            return ((Integer) userId).longValue();
        }

        if (userId instanceof Long) {
            return (Long) userId;
        }

        return Long.valueOf(userId.toString());
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public boolean isTokenValid(String token, Employee employee) {
        String username = extractUsername(token);
        return username.equals(employee.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expirationDate = extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }
}