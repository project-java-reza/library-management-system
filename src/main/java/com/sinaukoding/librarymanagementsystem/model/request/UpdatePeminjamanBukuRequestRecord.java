package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record UpdatePeminjamanBukuRequestRecord(
        String bukuId,

        LocalDate tanggalPinjam,

        LocalDate tanggalKembali,

        StatusBukuPinjaman statusBukuPinjaman,

        Long denda
) {
}
