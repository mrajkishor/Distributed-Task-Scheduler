package com.distributedscheduler.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {

    private static final String SECRET_KEY = "this-is-a-very-strong-jwt-secret-key-should-be-32+chars";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private final long EXPIRATION_MS = 1000 * 60 * 60; // 1 hour

    public String generateToken(String username, String role, String tenantId) {
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("tenantId", tenantId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public String extractTenantId(String token) {
        return extractAllClaims(token).get("tenantId", String.class);
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}



/**
 * use of this component
 *
 *
 * The `JWTUtil` component is a **JWT utility class** used for:
 *
 * ---
 *
 * ### ✅ **Main Purpose:**
 *
 * Handling **creation**, **validation**, and **parsing** of JWT (JSON Web Token) in your application.
 *
 * ---
 *
 * ### 🔧 **Functions Provided:**
 *
 * 1. **`generateToken(username, role, tenantId)`**
 *
 *    * Creates a signed JWT with:
 *
 *      * `username` as subject
 *      * `role` and `tenantId` as claims
 *      * 1-hour expiration
 *    * Used during login to issue tokens.
 *
 * 2. **`extractUsername(token)` / `extractRole(token)` / `extractTenantId(token)`**
 *
 *    * Reads values from token claims.
 *
 * 3. **`validateToken(token, userDetails)`**
 *
 *    * Checks if the token is valid:
 *
 *      * Matches the username
 *      * Is not expired
 *
 * 4. **`extractAllClaims(token)`** *(internal)*
 *
 *    * Decodes the JWT and returns all its claims.
 *
 * ---
 *
 * ### 🔐 **Why It Matters:**
 *
 * Used by your `JWTAuthenticationFilter` to:
 *
 * * Authenticate requests
 * * Extract roles and tenant info
 * * Enable **stateless security** (no session needed)
 *
 * ---
 *
 * ### 🧠 Summary:
 *
 * This component is the **core of your JWT security**. Without it, you cannot create or verify tokens securely.
 *
 * **/
