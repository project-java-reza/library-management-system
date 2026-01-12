package com.sinaukoding.librarymanagementsystem.model.filter;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;

import java.time.LocalDate;

public record PeminjamanBukuFilterRecord(User user,
                                         Buku buku,
                                         LocalDate tanggalPinjam,
                                         LocalDate tanggalKembali,
                                         StatusBukuPinjaman statusBukuPinjaman,
                                         String namaMahasiswa,
                                         String judulBuku) {
}
