package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum Role {

    PUSTAKAWAN("Pustakawan"),
    ANGGOTA("Anggota"),
    ADMIN("Admin");

    private final String label;

    Role(String label) {
        this.label = label;
    }

}
