package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;

    @Override
    public Admin create(AdminRequestRecord request) {
        // validasi mandatory
        validasiMandatory(request);

        // validasi data existing
        if (adminRepository.existsByEmail(request.email().toLowerCase())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        if (adminRepository.existsByUsername(request.username().toLowerCase())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        var admin = adminMapper.requestToEntity(request);
        admin.setPassword(passwordEncoder.encode(request.password()));
        adminRepository.save(admin);
        return admin;
    }

    private void validasiMandatory(AdminRequestRecord request) {
        if (request.nama() == null || request.nama().isEmpty()) {
            throw new RuntimeException("Nama tidak boleh kosong");
        }
        if (request.username() == null || request.username().isEmpty()) {
            throw new RuntimeException("Username tidak boleh kosong");
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }
        if (request.status() == null) {
            throw new RuntimeException("Status tidak boleh kosong");
        }
        if (request.role() == null) {
            throw new RuntimeException("Role tidak boleh kosong");
        }
    }
}
