package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum StatusBukuPinjaman {
    DIPINJAM("Dipinjam"),
    SUDAH_DIKEMBALIKAN("Sudah Dikembalikan"),
    DENDA("Denda"),
    HILANG("Hilang");

    private final String label;
    StatusBukuPinjaman(String label) {
        this.label = label;
    }

}
