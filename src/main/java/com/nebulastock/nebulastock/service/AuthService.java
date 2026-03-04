package com.nebulastock.nebulastock.service;

import com.nebulastock.nebulastock.dto.AuthResponse;
import com.nebulastock.nebulastock.dto.LoginRequest;
import com.nebulastock.nebulastock.dto.RegisterRequest;
import com.nebulastock.nebulastock.entity.User;
import com.nebulastock.nebulastock.exception.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.nebulastock.nebulastock.repository.UserRepository;
import com.nebulastock.nebulastock.security.JwtUtil;

@Service // Marks this as a Spring service Bean
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Injected from SecurityConfig
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        // Check if username already taken
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ApiException("User already exists", HttpStatus.CONFLICT);
        }

        // Build and save the user
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword())) // Hash it
                .role(request.getRole() != null ? request.getRole() : User.Role.VIEWER)
                .build();

        userRepository.save(user);

        //Generate token and return
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }

    public AuthResponse login(LoginRequest request) {
        // Find user or throw error
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED));

        // Check password matches the stored hash
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
        return new AuthResponse(token, user.getUsername(), user.getRole().name());
    }
}
