package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PeminjamanBukuRequestRecord(
        @NotBlank(message = "Buku ID tidak boleh kosong")
        String bukuId,

        @NotNull(message = "Tanggal Pinjam tidak boleh kosong")
        LocalDate tanggalPinjam,

        @NotNull(message = "Tanggal Kembali tidak boleh kosong")
        LocalDate tanggalKembali,

        @NotNull(message = "Status Buku Pinjaman tidak boleh kosong")
        StatusBukuPinjaman statusBukuPinjaman,

        Long denda
) {
}
