package com.sinaukoding.librarymanagementsystem.model.filter;

import com.sinaukoding.librarymanagementsystem.model.enums.StatusBuku;

public record BukuFilterRecord(String judulBuku,
                               String penulis,
                               String penerbit,
                               Integer tahunTerbit,
                               Integer jumlahSalinan,
                               String lokasiRak,
                               StatusBuku statusBuku) {
}
