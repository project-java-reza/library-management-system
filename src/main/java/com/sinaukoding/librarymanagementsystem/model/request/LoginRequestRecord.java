package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Login Request Model
 * Sesuai dengan frontend TypeScript interface LoginRequest
 */
public record LoginRequestRecord(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Password is required")
        String password
) {
}
