package com.distributedscheduler.config;

import com.distributedscheduler.security.CustomUserDetailsService;
import com.distributedscheduler.security.JWTAuthenticationFilter;
import com.distributedscheduler.security.JWTUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Enables @PreAuthorize annotations
public class SecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Updated for Spring Security 6+
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()   // Public login/register
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin-only
                        .anyRequest().authenticated()              // All others need auth
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class); // Add JWT filter

        return http.build();
    }


    @Bean
    public JWTAuthenticationFilter jwtFilter() {
        return new JWTAuthenticationFilter(new JWTUtil(), new CustomUserDetailsService());
    }

}
