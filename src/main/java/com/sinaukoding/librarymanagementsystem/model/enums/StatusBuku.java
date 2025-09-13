package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum StatusBuku {
    TERSEDIA("Tersedia"),
    DIPINJAM("Dipinjam"),
    SUDAH_DIBOOKING("Sudah Dibooking"),
    HILANG("Hilang");

    private final String label;

    StatusBuku(String label) {
        this.label = label;
    }
}
