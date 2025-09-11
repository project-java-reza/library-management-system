package com.sinaukoding.librarymanagementsystem.model.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestRecord(@NotBlank String username,
                                 @NotBlank String password) {
}