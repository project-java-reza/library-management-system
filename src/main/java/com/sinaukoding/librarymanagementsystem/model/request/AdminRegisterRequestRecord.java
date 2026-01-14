package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminRegisterRequestRecord(
        @NotBlank(message = "Nama tidak boleh kosong")
        String nama,

        @NotBlank(message = "Username tidak boleh kosong")
        @Size(min = 3, max = 50, message = "Username harus 3-50 karakter")
        String username,

        @NotBlank(message = "Email tidak boleh kosong")
        @Email(message = "Email harus valid")
        String email,

        @NotBlank(message = "Password tidak boleh kosong")
        @Size(min = 6, message = "Password harus minimal 6 karakter")
        String password
) {
}
