package com.sinaukoding.librarymanagementsystem.model.filter;

import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;

public record AdminFilterRecord(String nama,
                                String email,
                                String username,
                                Status status,
                                ERole role) {
}
