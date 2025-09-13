package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum StatusBuku {
    TERSEDIA("Tersedia"),
    TIDAK_TERSEDIA("Tidak Tersedia"),
    SUDAH_DIBOOKING("Sudah Dibooking");

    private final String label;

    StatusBuku(String label) {
        this.label = label;
    }
}
