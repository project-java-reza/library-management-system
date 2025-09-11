package com.sinaukoding.librarymanagementsystem.model.filter;


import com.sinaukoding.librarymanagementsystem.model.enums.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;

public record UserFilterRecord(String nama,
                               String email,
                               String username,
                               Status status,
                               Role role) {
}