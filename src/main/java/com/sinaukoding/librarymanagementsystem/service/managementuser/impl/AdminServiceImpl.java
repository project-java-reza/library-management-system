package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.ChangePasswordRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.service.FileStorageService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RoleService roleService;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminMapper adminMapper;
    private final FileStorageService fileStorageService;

    @Override
    public Admin create(AdminRequestRecord request) {
        // Check if email already exists
        if (adminRepository.existsByEmail(request.email().toLowerCase())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        // Check if username already exists
        if (adminRepository.existsByUsername(request.username().toLowerCase())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        Role adminRole = roleService.getOrSave(ERole.ADMIN);

        var admin = adminMapper.requestToEntity(request);
        admin.setUsername(request.username().toLowerCase());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setEmail(request.email().toLowerCase());
        admin.setRole(adminRole);
        adminRepository.save(admin);
        return admin;
    }

    @Override
    public Admin edit(AdminRequestRecord request) {
        var adminExisting = adminRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Data admin tidak ditemukan"));

        // Check if email is already used by another admin
        if (adminRepository.existsByEmailAndIdNot(request.email().toLowerCase(), request.id())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        // Check if username is already used by another admin
        if (adminRepository.existsByUsernameAndIdNot(request.username().toLowerCase(), request.id())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        var admin = adminMapper.requestToEntity(request);
        admin.setId(adminExisting.getId());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setNama(request.nama());
        admin.setUsername(request.username().toLowerCase());
        admin.setEmail(request.email().toLowerCase());
        admin.setStatus(adminExisting.getStatus()); // Preserve existing status
        adminRepository.save(admin);
        return admin;
    }

    @Override
    public Admin editProfile(AdminProfileRequestRecord request) {
        // Get admin by ID
        var adminExisting = adminRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Data admin tidak ditemukan"));

        // Only check uniqueness if the value is provided and different
        if (request.username() != null && !request.username().isEmpty()) {
            if (!request.username().equals(adminExisting.getUsername()) &&
                adminRepository.existsByUsernameAndIdNot(request.username().toLowerCase(), request.id())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }
        }

        if (request.email() != null && !request.email().isEmpty()) {
            if (!request.email().equals(adminExisting.getEmail()) &&
                adminRepository.existsByEmailAndIdNot(request.email().toLowerCase(), request.id())) {
                throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
            }
        }

        // Update only non-null fields - partial update
        if (request.nama() != null && !request.nama().isEmpty()) {
            adminExisting.setNama(request.nama());
        }

        if (request.username() != null && !request.username().isEmpty()) {
            adminExisting.setUsername(request.username().toLowerCase());
        }

        if (request.email() != null && !request.email().isEmpty()) {
            adminExisting.setEmail(request.email().toLowerCase());
        }

        if (request.fotoUrl() != null && !request.fotoUrl().isEmpty()) {
            adminExisting.setFotoUrl(request.fotoUrl());
            adminExisting.setFotoUploadDate(java.time.LocalDateTime.now());
        }

        adminRepository.save(adminExisting);
        return adminExisting;
    }

    @Override
    public String uploadFoto(String id, MultipartFile file) {
        var admin = adminRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data admin tidak ditemukan"));

        String filePath = fileStorageService.storeFile(file, "admin");
        admin.setFotoUrl(filePath);
        admin.setFotoUploadDate(LocalDateTime.now());
        adminRepository.save(admin);

        return filePath;
    }

    @Override
    public SimpleMap findByUsername(String username) {
        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Data admin tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", admin.getId());
        data.put("nama", admin.getNama());
        data.put("username", admin.getUsername());
        data.put("email", admin.getEmail());
        data.put("status", admin.getStatus().getLabel());
        data.put("role", admin.getRole().getRole().getLabel());
        data.put("fotoUrl", admin.getFotoUrl());
        data.put("fotoUploadDate", admin.getFotoUploadDate());
        return data;
    }

    @Override
    public void changePassword(ChangePasswordRequestRecord request) {
        var admin = adminRepository.findById(request.id())
                .orElseThrow(() -> new RuntimeException("Data admin tidak ditemukan"));

        // Verify old password
        if (!passwordEncoder.matches(request.oldPassword(), admin.getPassword())) {
            throw new RuntimeException("Password lama tidak sesuai");
        }

        // Encode and set new password
        admin.setPassword(passwordEncoder.encode(request.newPassword()));
        adminRepository.save(admin);
    }

    @Override
    public Page<SimpleMap> findAllProfileAdmin(AdminFilterRecord filterRequest, Pageable pageable) {

        CustomBuilder<Admin> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("nama", filterRequest.nama(), builder);
        FilterUtil.builderConditionNotBlankLike("email", filterRequest.email(), builder);
        FilterUtil.builderConditionNotBlankLike("username", filterRequest.username(), builder);
        FilterUtil.builderConditionNotNullEqual("status", filterRequest.status(), builder);
        FilterUtil.builderConditionNotNullEqual("role", filterRequest.role(), builder);

        Page<Admin> listAdmin = adminRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listAdmin.stream().map(user -> {
            SimpleMap data = new SimpleMap();
            data.put("id", user.getId());
            data.put("nama", user.getNama());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("role", user.getRole().getRole().getLabel());
            data.put("status", user.getStatus().getLabel());
            return data;
        }).toList();

        return AppPage.create(listData, pageable, listAdmin.getTotalElements());
    }

    @Override
    public SimpleMap findById(String id) {
        var admin = adminRepository.findById(id).orElseThrow(() ->  new RuntimeException("Data admin tidak ditemukan"));
        SimpleMap data = new SimpleMap();
        data.put("id", admin.getId());
        data.put("nama", admin.getNama());
        data.put("username", admin.getUsername());
        data.put("email", admin.getEmail());
        data.put("status", admin.getStatus().getLabel());
        data.put("role", admin.getRole().getRole().getLabel());
        data.put("fotoUrl", admin.getFotoUrl());
        data.put("fotoUploadDate", admin.getFotoUploadDate());
        return data;
    }

    @Override
    public void deleteByIdAdmin(String id) {
        var admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        adminRepository.deleteById(id);
    }
}
