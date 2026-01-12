package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request Record untuk CREATE Buku baru
 * Semua field WAJIB diisi
 */
public record CreateBukuRequestRecord(
        @NotBlank(message = "Judul Buku tidak boleh kosong") String judulBuku,
        @NotBlank(message = "Penulis tidak boleh kosong") String penulis,
        @NotBlank(message = "Penerbit tidak boleh kosong") String penerbit,
        @NotNull(message = "Tahun Terbit tidak boleh kosong") Integer tahunTerbit,
        String isbn,
        @NotBlank(message = "Kategori ID tidak boleh kosong") String kategoriId,
        @NotNull(message = "Jumlah Salinan tidak boleh kosong") Integer jumlahSalinan,
        String deskripsi,
        // Lokasi Rak - 5 field terpisah, SEMUA WAJIB diisi
        @NotBlank(message = "Lantai tidak boleh kosong") String lantai,
        @NotBlank(message = "Ruang/Zona tidak boleh kosong") String ruang,
        @NotBlank(message = "Rak tidak boleh kosong") String rak,
        @NotBlank(message = "Nomor Rak tidak boleh kosong") String nomorRak,
        @NotBlank(message = "Nomor Baris tidak boleh kosong") String nomorBaris) {
}
