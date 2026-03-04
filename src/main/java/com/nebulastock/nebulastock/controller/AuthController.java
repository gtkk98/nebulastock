package com.nebulastock.nebulastock.controller;

import com.nebulastock.nebulastock.dto.AuthResponse;
import com.nebulastock.nebulastock.dto.LoginRequest;
import com.nebulastock.nebulastock.dto.RegisterRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.nebulastock.nebulastock.service.AuthService;

@RestController // Combines @Controller + @ResponseBody (returns JSON automatically)
@RequestMapping("/api/auth") // Base URL for all methods in this controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register") // POST /api/auth/register
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) { // @Valid triggers validation, @RequestBody parses JSON
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
    }

    @PostMapping("/login") // POST /api/auth/login
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
