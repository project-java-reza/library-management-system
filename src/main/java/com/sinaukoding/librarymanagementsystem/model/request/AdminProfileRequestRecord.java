package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdminProfileRequestRecord(
        @NotBlank(message = "ID tidak boleh kosong")
        String id,

        @Size(min = 3, max = 50, message = "Username harus 3-50 karakter")
        String username,

        String nama,

        @Email(message = "Email harus valid")
        String email,

        String fotoUrl
) {
}
