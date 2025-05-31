package com.distributedscheduler.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;

    public JWTAuthenticationFilter(JWTUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtUtil.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String role = jwtUtil.extractAllClaims(token).get("role", String.class);
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

            if (jwtUtil.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}


/**
 *
 * The `JWTAuthenticationFilter` is a **custom Spring Security filter** that processes **JWT tokens** on every HTTP request to authenticate users.
 *
 * ---
 *
 * ### ✅ **Purpose:**
 *
 * It intercepts each request, checks for a valid JWT token in the `Authorization` header, and sets up Spring Security's authentication context.
 *
 * ---
 *
 * ### 🔄 **How It Works (Step-by-Step):**
 *
 * 1. **Extract Token:**
 *
 *    * Reads `Authorization` header.
 *    * If header starts with `Bearer `, it extracts the token part.
 *
 * 2. **Extract Username from Token:**
 *
 *    * Uses `jwtUtil.extractUsername(token)`.
 *
 * 3. **Authenticate User (if not already authenticated):**
 *
 *    * Loads `UserDetails` from `CustomUserDetailsService`.
 *    * Extracts the role from token claims.
 *    * Validates token using `jwtUtil.validateToken()`.
 *
 * 4. **Set Authentication:**
 *
 *    * Creates a `UsernamePasswordAuthenticationToken`.
 *    * Sets it in the `SecurityContextHolder`.
 *
 * 5. **Continue the Filter Chain:**
 *
 *    * Passes the request to the next filter/controller.
 *
 * ---
 *
 * ### 🧠 **Why Important:**
 *
 * * Ensures **secure, stateless** user authentication using JWT.
 * * Without this, Spring Security wouldn’t recognize the user from the JWT token in API requests.
 *
 * ---
 *
 * ### 🔐 Example:
 *
 * If a request has this header:
 *
 * ```
 * Authorization: Bearer <your-jwt-token>
 * ```
 *
 * The filter:
 *
 * * Validates the token,
 * * Extracts user details and role,
 * * Authenticates the user for that request.
 *
 *
 *
 *
 * **/