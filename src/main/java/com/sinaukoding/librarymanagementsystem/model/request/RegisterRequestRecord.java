package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Register Request Model
 * Sesuai dengan frontend TypeScript interface RegisterRequest
 */
public record RegisterRequestRecord(
        @NotBlank(message = "Username is required")
        String username,

        @NotBlank(message = "Email is required")
        @Email(message = "Email should be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password should be at least 6 characters")
        String password,

        @NotBlank(message = "Nama is required")
        String nama,

        @NotBlank(message = "NIM is required")
        String nim,

        @NotBlank(message = "Jurusan is required")
        String jurusan
) {
}
