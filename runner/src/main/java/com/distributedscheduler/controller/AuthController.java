package com.distributedscheduler.controller;

import com.distributedscheduler.security.JWTUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTUtil jwtUtil;

    public AuthController(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> loginRequest) {
        String username = loginRequest.get("username");
        String role = loginRequest.get("role");
        String tenantId = loginRequest.get("tenantId");

        // NOTE: You can add authentication logic here if needed
        String token = jwtUtil.generateToken(username, role, tenantId);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
