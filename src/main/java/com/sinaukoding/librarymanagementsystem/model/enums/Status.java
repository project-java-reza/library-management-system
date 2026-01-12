package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum Status {

    AKTIF("AKTIF"),
    TIDAK_AKTIF("TIDAK_AKTIF");

    private final String label;

    Status(String label) {
        this.label = label;
    }

}

