package com.sinaukoding.librarymanagementsystem.model.request;

public record AdminRegisterRequestRecord(
        String nama,
        String username,
        String email,
        String password
) {
}
