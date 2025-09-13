package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.model.enums.StatusBuku;

public record BukuRequestRecord(String judulBuku,
                                String penulis,
                                String penerbit,
                                Integer tahunTerbit,
                                Integer jumlahSalinan,
                                String lokasiRak,
                                String kategoriBukuId,
                                StatusBuku statusBuku) {
}
