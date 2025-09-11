package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum ERole {

    ANGGOTA("Anggota"),
    ADMIN("Admin");

    private final String label;

    ERole(String label) {
        this.label = label;
    }

}
