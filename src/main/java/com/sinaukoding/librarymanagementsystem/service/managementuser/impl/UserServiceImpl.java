package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.UserMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.FileStorageService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;
    private final MahasiswaRepository mahasiswaRepository;

    @Override
    public User add(UserRequestRecord request) {
        if (userRepository.existsByEmail(request.email().toLowerCase())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        if (userRepository.existsByUsername(request.username().toLowerCase())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        var user = userMapper.requestToEntity(request);
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setTanggalBergabung(LocalDate.now());
        userRepository.save(user);

        // Handle Mahasiswa data if provided
        if (request.nim() != null || request.notelepon() != null || request.jurusan() != null || request.alamat() != null) {
            Mahasiswa mahasiswa = Mahasiswa.builder()
                    .nim(request.nim())
                    .phoneNumber(request.notelepon())
                    .jurusan(request.jurusan())
                    .alamat(request.alamat())
                    .nama(request.nama())
                    .user(user)
                    .build();
            mahasiswaRepository.save(mahasiswa);
        }

        return user;
    }

    @Override
    public User edit(UserRequestRecord request) {
        var userExisting = userRepository.findById(request.id()).orElseThrow(() ->  new RuntimeException("Data user tidak ditemukan"));

        if (userRepository.existsByEmailAndIdNot(request.email().toLowerCase(), request.id())) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
        }
        if (userRepository.existsByUsernameAndIdNot(request.username().toLowerCase(),  request.id())) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
        }

        // Update existing user instead of creating new entity
        userExisting.setNama(request.nama());
        userExisting.setUsername(request.username().toLowerCase());
        userExisting.setEmail(request.email().toLowerCase());
        userExisting.setPassword(passwordEncoder.encode(request.password()));
        userExisting.setStatus(request.status());
        // Preserve existing role - don't update it from request
        userRepository.save(userExisting);

        // Handle Mahasiswa data if provided
        if (request.nim() != null || request.notelepon() != null || request.jurusan() != null || request.alamat() != null) {
            // Load existing mahasiswa with user to avoid lazy initialization
            var userWithMahasiswa = userRepository.findByIdWithMahasiswa(userExisting.getId())
                    .orElse(userExisting);

            if (userWithMahasiswa.getMahasiswa() != null) {
                // Update existing mahasiswa
                Mahasiswa mahasiswa = userWithMahasiswa.getMahasiswa();
                if (request.nim() != null) mahasiswa.setNim(request.nim());
                if (request.notelepon() != null) mahasiswa.setPhoneNumber(request.notelepon());
                if (request.jurusan() != null) mahasiswa.setJurusan(request.jurusan());
                if (request.alamat() != null) mahasiswa.setAlamat(request.alamat());
                mahasiswa.setNama(request.nama());
                mahasiswaRepository.save(mahasiswa);
            } else {
                // Create new mahasiswa
                Mahasiswa newMahasiswa = Mahasiswa.builder()
                        .nim(request.nim())
                        .phoneNumber(request.notelepon())
                        .jurusan(request.jurusan())
                        .alamat(request.alamat())
                        .nama(request.nama())
                        .user(userExisting)
                        .build();
                mahasiswaRepository.save(newMahasiswa);
            }
        }

        return userExisting;
    }

    @Override
    public Page<SimpleMap> findAllProfileUser(UserFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<User> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("nama", filterRequest.nama(), builder);
        FilterUtil.builderConditionNotBlankLike("email", filterRequest.email(), builder);
        FilterUtil.builderConditionNotBlankLike("username", filterRequest.username(), builder);
        FilterUtil.builderConditionNotNullEqual("status", filterRequest.status(), builder);
        FilterUtil.builderConditionNotNullEqual("role", filterRequest.role(), builder);

        Page<User> listUser = userRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listUser.stream().map(user -> {
            SimpleMap data = new SimpleMap();
            data.put("id", user.getId());
            data.put("nama", user.getNama());
            data.put("username", user.getUsername());
            data.put("email", user.getEmail());
            data.put("role", user.getStatus().getLabel());
            data.put("status", user.getRole().getRole().getLabel());
            data.put("tanggalBergabung", user.getTanggalBergabung());

            // Mahasiswa data
            if (user.getMahasiswa() != null) {
                data.put("nim", user.getMahasiswa().getNim());
                data.put("jurusan", user.getMahasiswa().getJurusan());
                data.put("alamat", user.getMahasiswa().getAlamat());
                data.put("notelepon", user.getMahasiswa().getPhoneNumber());
            } else {
                data.put("nim", null);
                data.put("jurusan", null);
                data.put("alamat", null);
                data.put("notelepon", null);
            }

            return data;
        }).toList();

        return AppPage.create(listData, pageable, listUser.getTotalElements());
    }

    @Override
    public SimpleMap findByIdUser(String id) {
        var user = userRepository.findByIdWithMahasiswa(id)
                .orElseThrow(() -> new RuntimeException("Data user tidak ditemukan"));
        SimpleMap data = new SimpleMap();
        data.put("id", user.getId());
        data.put("nama", user.getNama());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("status", user.getStatus().getLabel());
        data.put("role", user.getRole().getRole().getLabel());
        data.put("fotoUrl", user.getFotoUrl());
        data.put("fotoUploadDate", user.getFotoUploadDate());
        data.put("tanggalBergabung", user.getTanggalBergabung());

        // Mahasiswa data
        if (user.getMahasiswa() != null) {
            data.put("nim", user.getMahasiswa().getNim());
            data.put("jurusan", user.getMahasiswa().getJurusan());
            data.put("alamat", user.getMahasiswa().getAlamat());
            data.put("notelepon", user.getMahasiswa().getPhoneNumber());
        } else {
            data.put("nim", null);
            data.put("jurusan", null);
            data.put("alamat", null);
            data.put("notelepon", null);
        }

        return data;
    }

    @Override
    public void deleteByIdUser(String id) {
        var user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User tidak ditemukan"));
        userRepository.deleteById(id);
    }

    @Override
    public SimpleMap getProfileByUsername(String username) {
        var user = userRepository.findByUsernameWithMahasiswa(username)
                .orElseThrow(() -> new RuntimeException("User dengan username '" + username + "' tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", user.getId());
        data.put("nama", user.getNama());
        data.put("username", user.getUsername());
        data.put("email", user.getEmail());
        data.put("status", user.getStatus().getLabel());
        data.put("role", user.getRole().getRole().getLabel());
        data.put("fotoUrl", user.getFotoUrl());
        data.put("fotoUploadDate", user.getFotoUploadDate());
        data.put("tanggalBergabung", user.getTanggalBergabung());

        // Mahasiswa data
        if (user.getMahasiswa() != null) {
            data.put("nim", user.getMahasiswa().getNim());
            data.put("jurusan", user.getMahasiswa().getJurusan());
            data.put("alamat", user.getMahasiswa().getAlamat());
            data.put("notelepon", user.getMahasiswa().getPhoneNumber());
        } else {
            data.put("nim", null);
            data.put("jurusan", null);
            data.put("alamat", null);
            data.put("notelepon", null);
        }

        return data;
    }

    @Override
    public User updateProfileByUsername(UserProfileRequestRecord request, String username) {
        var userExisting = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User dengan username '" + username + "' tidak ditemukan"));

        // Only check uniqueness if the value is provided and different
        if (request.username() != null && !request.username().isEmpty()) {
            if (!request.username().equals(userExisting.getUsername()) &&
                userRepository.existsByUsernameAndIdNot(request.username().toLowerCase(), userExisting.getId())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }
        }

        if (request.email() != null && !request.email().isEmpty()) {
            if (!request.email().equals(userExisting.getEmail()) &&
                userRepository.existsByEmailAndIdNot(request.email().toLowerCase(), userExisting.getId())) {
                throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
            }
        }

        // Update only non-null fields - partial update
        if (request.nama() != null && !request.nama().isEmpty()) {
            userExisting.setNama(request.nama());
        }

        if (request.username() != null && !request.username().isEmpty()) {
            userExisting.setUsername(request.username().toLowerCase());
        }

        if (request.email() != null && !request.email().isEmpty()) {
            userExisting.setEmail(request.email().toLowerCase());
        }

        if (request.fotoUrl() != null && !request.fotoUrl().isEmpty()) {
            userExisting.setFotoUrl(request.fotoUrl());
            userExisting.setFotoUploadDate(LocalDateTime.now());
        }

        userRepository.save(userExisting);
        return userExisting;
    }

    @Override
    public String uploadFoto(String id, MultipartFile file) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Data user tidak ditemukan"));

        String filePath = fileStorageService.storeFile(file, "user");
        user.setFotoUrl(filePath);
        user.setFotoUploadDate(LocalDateTime.now());
        userRepository.save(user);

        return filePath;
    }

}