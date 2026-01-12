package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record IdRequestRecord(
        @NotBlank(message = "ID cannot be blank")
        String id
) {
}
