package com.sinaukoding.librarymanagementsystem.model.request;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;

import java.time.LocalDate;

public record PeminjamanBukuRequestRecord(User user,
                                          Buku buku,
                                          LocalDate tanggalPinjam,
                                          LocalDate tanggalKembali,
                                          Status statusBuku){
}
