package com.hackathon.medreminder.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Authentication response containing the token details and user info")
public record AuthResponse(
        @Schema(description = "Response message", example = "Authentication successful")
        String message,

        @Schema(description = "Type of the token", example = "Bearer")
        String tokenType,

        @Schema(description = "Access token string", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9")
        String token,

        @Schema(description = "Username associated with the token", example = "john.doe")
        String username,

        @Schema(description = "Refresh token string", example = "dGhpc2lzYXJlZnJlc2h0b2tlbg==")
        String refreshToken
) {}
