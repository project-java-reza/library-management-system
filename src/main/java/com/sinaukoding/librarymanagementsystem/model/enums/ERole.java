package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum ERole {

    ANGGOTA("ANGGOTA"),
    ADMIN("ADMIN");

    private final String label;

    ERole(String label) {
        this.label = label;
    }

}
