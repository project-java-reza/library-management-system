package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record MahasiswaProfileRequestRecord(
        @NotBlank(message = "Nama cannot be blank")
        String nama,

        @NotBlank(message = "NIM cannot be blank")
        String nim,

        @NotBlank(message = "Jurusan cannot be blank")
        String jurusan,

        @NotBlank(message = "Alamat cannot be blank")
        String alamat,

        @NotBlank(message = "Phone number cannot be blank")
        String phoneNumber
) {
}
