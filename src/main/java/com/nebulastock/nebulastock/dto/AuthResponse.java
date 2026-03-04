package com.nebulastock.nebulastock.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token; // The JWT token sent back to client
    private String username;
    private String role;
}
