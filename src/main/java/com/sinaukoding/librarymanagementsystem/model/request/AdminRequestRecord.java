package com.sinaukoding.librarymanagementsystem.model.request;


import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record AdminRequestRecord(
        String id,

        @NotBlank(message = "Nama tidak boleh kosong")
        String nama,

        @NotBlank(message = "Username tidak boleh kosong")
        @Size(min = 3, max = 50, message = "Username harus 3-50 karakter")
        String username,

        @NotBlank(message = "Email tidak boleh kosong")
        @Email(message = "Email harus valid")
        String email,

        @NotBlank(message = "Password tidak boleh kosong")
        @Size(min = 6, message = "Password minimal 6 karakter")
        String password,

        @NotNull(message = "Role tidak boleh kosong")
        ERole role
) {
}
