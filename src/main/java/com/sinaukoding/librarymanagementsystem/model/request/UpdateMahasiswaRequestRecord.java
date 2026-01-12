package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateMahasiswaRequestRecord(
        @NotBlank(message = "ID cannot be blank")
        String id,

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

        String email,

        String username,

        String password,

        String status
) {
}
