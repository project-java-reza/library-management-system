package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record BukuSearchRequestRecord(
        // Sorting parameters
        String sortColumn,
        String sortColumnDir,

        // Pagination parameters
        @NotNull(message = "Page number cannot be null")
        @Min(value = 1, message = "Page number must be at least 1")
        Integer pageNumber,

        @NotNull(message = "Page size cannot be null")
        @Min(value = 1, message = "Page size must be at least 1")
        Integer pageSize,

        // Search parameters
        String search,         // Global search in judul, penulis, penerbit, isbn
        String judulBuku,      // Search by judul only
        String penulis,        // Search by penulis only
        String penerbit,       // Search by penerbit only
        String isbn,           // Search by isbn only
        String kategoriId      // Filter by kategori buku id
) {
}
