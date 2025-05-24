package com.distributedscheduler.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Example only – replace with DB logic
        if (username.equals("admin")) {
            return User.builder()
                    .username("admin")
                    .password("{noop}password") // {noop} disables encoding
                    .roles("ADMIN")
                    .build();
        } else {
            return User.builder()
                    .username("user")
                    .password("{noop}password")
                    .roles("USER")
                    .build();
        }
    }
}
