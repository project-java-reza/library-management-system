package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record CreateMahasiswaRequestRecord(
        @NotBlank(message = "Nama cannot be blank")
        String nama,

        @NotBlank(message = "NIM cannot be blank")
        String nim,

        @NotBlank(message = "Jurusan cannot be blank")
        String jurusan,

        @NotBlank(message = "Alamat cannot be blank")
        String alamat,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber,

        @NotBlank(message = "Email cannot be blank")
        String email,

        @NotBlank(message = "Username cannot be blank")
        String username,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
