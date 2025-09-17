package com.hackathon.medreminder.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserRegisterResponse", description = "Response returned after successful user registration")
public record RegisterResponse(

        @Schema(description = "Unique identifier of the user", example = "123", required = true)
        Integer id,

        @Schema(description = "User's first name", example = "Alice", required = true)
        String firstName,

        @Schema(description = "User's username", example = "alice123", required = true)
        String username,

        @Schema(description = "User's last name", example = "Smith", required = true)
        String lastName,

        @Schema(description = "User's email address", example = "alice@example.com", required = true)
        String email,

        @Schema(description = "Success message", example = "User successfully registered", required = true)
        String message
) {
}
