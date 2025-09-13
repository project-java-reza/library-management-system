package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum EStatusBuku {

    TERSEDIA("Tersedia"),
    TIDAK_TERSEDIA("Tidak Tersedia");

    private final String label;

    EStatusBuku(String label) {
        this.label = label;
    }
}
