package com.nebulastock.nebulastock.dto;

import com.nebulastock.nebulastock.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data // Lombok: getter and setter
public class RegisterRequest {
    @NotBlank(message = "Username is required") // validation cannot be empty
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at leats 6 characters")
    private String password;

    private User.Role role; // // Optional — defaults to VIEWER if not provided
}
