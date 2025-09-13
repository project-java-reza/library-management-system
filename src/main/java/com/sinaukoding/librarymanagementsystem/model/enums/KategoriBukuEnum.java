package com.sinaukoding.librarymanagementsystem.model.enums;

import lombok.Getter;

@Getter
public enum KategoriBukuEnum {
    FIKSI ("Fiksi"),
    NON_FIKSI ("Non-Fiksi"),
    BIOGRAFI ("Biografi"),
    SEJARAH ("Sejarah"),
    SAINS ("Sains"),
    TEKNOLOGI ("Teknologi"),
    SENI ("Seni"),
    BUDAYA ("Budaya"),
    AGAMA ("Agama"),
    PSIKOLOGI ("Psikologi"),
    EKONOMI ("Ekonomi"),
    POLITIK ("Politik"),
    HUKUM ("Hukum"),
    PENDIDIKAN("Pendidikan"),
    KESEHATAN("Kesehatan"),
    OLAHRAGA("Olahraga"),
    KULINER ("Kuliner"),
    PERJALANAN ("Perjalanan"),
    ANAK_ANAK ("Anak-Anak"),
    REMAJA ("Remaja"),
    ROMANCE ("Romance"),
    FANTASI ("Fantasi"),
    MISTERI ("Misteri");

    private final String label;

    KategoriBukuEnum(String label) {
        this.label = label;
    }
}

