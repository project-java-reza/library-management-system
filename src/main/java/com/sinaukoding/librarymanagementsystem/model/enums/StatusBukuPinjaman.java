package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum StatusBukuPinjaman {
    PENDING("Pending"),
    DIPINJAM("Dipinjam"),
    DENDA("Terlambat"),
    SUDAH_DIKEMBALIKAN("Sudah Dikembalikan");

    private final String label;
    StatusBukuPinjaman(String label) {
        this.label = label;
    }

}
