package com.sinaukoding.librarymanagementsystem.model.request;


import com.sinaukoding.librarymanagementsystem.model.enums.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;

public record UserRequestRecord(String id,
                                String nama,
                                String username,
                                String email,
                                String password,
                                Status status,
                                Role role) {
}