package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SearchKategoriBukuRequestRecord(
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
        String search         // Global search
) {
}
