package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final RoleService roleService;
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

        Role adminRole = roleService.getOrSave(ERole.ADMIN);

        var admin = adminMapper.requestToEntity(request);
        admin.setUsername(request.username());
        admin.setPassword(passwordEncoder.encode(request.password()));
        admin.setEmail(request.email());
        admin.setRole(adminRole);
        admin.setStatus(request.status());
        adminRepository.save(admin);
        return admin;
    }

    @Override
    public Admin edit(AdminRequestRecord request) {
        // validasi mandatory
        validasiMandatory(request);

        var adminExisting = adminRepository.findById(request.id()).orElseThrow(() ->  new RuntimeException("Data user tidak ditemukan"));

        // validasi data existing
        if (adminRepository.existsByEmailAndIdNot(request.email().toLowerCase(), request.id())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        if (adminRepository.existsByUsernameAndIdNot(request.username().toLowerCase(),  request.id())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        var admin = adminMapper.requestToEntity(request);
        admin.setId(adminExisting.getId());
        admin.setPassword(passwordEncoder.encode(request.password()));
        adminRepository.save(admin);
        return admin;
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
            data.put("role", user.getStatus().getLabel());
            data.put("status", user.getRole().getRole().getLabel());
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
        return data;
    }

    @Override
    public void deleteByIdAdmin(String id) {
        var admin = adminRepository.findById(id).orElseThrow(() -> new RuntimeException("Admin tidak ditemukan"));
        adminRepository.deleteById(id);
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
