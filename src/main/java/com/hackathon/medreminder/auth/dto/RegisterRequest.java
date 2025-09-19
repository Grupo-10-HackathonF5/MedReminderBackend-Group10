package com.hackathon.medreminder.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer Object for user registration")
public record RegisterRequest(

        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must be at most 50 characters")
        @Schema(description = "User's first name", example = "Alice", required = true)
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must be at most 50 characters")
        @Schema(description = "User's last name", example = "Smith", required = true)
        String lastName,

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        @Schema(description = "User's username", example = "alice123", required = true)
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        @Schema(description = "User's email address", example = "alice@example.com", required = true)
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
        @Schema(description = "User's password", example = "password123", required = true)
        String password
) {}
