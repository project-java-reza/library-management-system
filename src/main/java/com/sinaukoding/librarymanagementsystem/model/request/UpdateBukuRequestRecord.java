package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;

/**
 * Request Record untuk UPDATE Buku yang sudah ada
 * Semua field OPSIONAL - hanya field yang dikirim yang akan diupdate
 */
public record UpdateBukuRequestRecord(
        String id,
        String judulBuku,
        String penulis,
        String penerbit,
        Integer tahunTerbit,
        String isbn,
        String kategoriId,
        Integer jumlahSalinan,
        String deskripsi,
        // Lokasi Rak - 5 field terpisah, SEMUA OPSIONAL
        // Jika ingin update lokasi, semua 5 field harus dikirim
        String lantai,
        String ruang,
        String rak,
        String nomorRak,
        String nomorBaris,
        EStatusBuku statusBuku) {
}
