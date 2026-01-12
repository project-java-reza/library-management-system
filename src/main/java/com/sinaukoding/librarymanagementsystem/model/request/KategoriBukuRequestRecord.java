package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record KategoriBukuRequestRecord(
        String id,

        @NotBlank(message = "Nama Kategori tidak boleh kosong")
        String nama,

        String deskripsi
) {
}
