package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;

import java.time.LocalDate;

public record PeminjamanBukuRequestRecord(String bukuId,
                                          LocalDate tanggalPinjam,
                                          LocalDate tanggalKembali,
                                          StatusBukuPinjaman statusBukuPinjaman,
                                          Long denda){
}
