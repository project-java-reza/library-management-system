package com.sinaukoding.librarymanagementsystem.model.request;

public record SearchRecentPeminjamanRequestRecord(
    String sortColumn,
    String sortColumnDir,
    Integer pageNumber,
    Integer pageSize
) {
}
