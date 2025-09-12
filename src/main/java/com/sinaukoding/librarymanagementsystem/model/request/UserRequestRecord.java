package com.sinaukoding.librarymanagementsystem.model.request;


import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;

public record UserRequestRecord(String id,
                                String nama,
                                String username,
                                String email,
                                String password,
                                Status status,
                                ERole role,
                                Mahasiswa mahasiswa) {
}