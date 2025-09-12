package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.master.MahasiswaMapper;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MahasiswaServiceImpl implements MahasiswaService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MahasiswaMapper mahasiswaMapper;
    private final MahasiswaRepository mahasiswaRepository;

    @Override
    public Mahasiswa addProfileMahasiswaUser(MahasiswaRequestRecord request, String token) {

        String rawToken = token;
        if (rawToken != null && rawToken.startsWith("Bearer ")) {
            rawToken = rawToken.substring(7);
        }

        String username = jwtUtil.extractUsername(rawToken);
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Token tidak valid: username kosong.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User dengan username " + username + " tidak ditemukan."));

        if (user.getMahasiswa() != null) {
            throw new IllegalStateException("User ini sudah memiliki profil Mahasiswa.");
        }

        validasiMandatory(request);

        Mahasiswa mahasiswa = mahasiswaMapper.requestToEntity(request);

        mahasiswa.setNim(request.nim());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());

        // 7) Set relasi dua arah (penting!)
        mahasiswa.setUser(user);     // owning side
        user.setMahasiswa(mahasiswa);

        // 8) Simpan. Karena owning side adalah Mahasiswa, pastikan save Mahasiswa.
        //    Jika TIDAK ada cascade dari User -> Mahasiswa, simpan keduanya.
        Mahasiswa saved = mahasiswaRepository.save(mahasiswa);
        userRepository.save(user);

        return saved;
    }
//
//    @Override
//    public Mahasiswa editProfileMahasiswaUser(MahasiswaRequestRecord request) {
//        return null;
//    }
//
//    @Override
//    public Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaRequestRecord filterRequest, Pageable pageable) {
//        return null;
//    }
//
        @Override
        public SimpleMap findByIdMahasiswaUser(String id) {
            return null;
        }

//    @Override
//    public void deleteByIdMahasiswaUser(String id) {
//
//    }
//
    private void validasiMandatory(MahasiswaRequestRecord request) {
        if (request.nim() == null || request.nim().isEmpty()) {
            throw new RuntimeException("NIM tidak boleh kosong");
        }
        if (request.jurusan() == null || request.jurusan().isEmpty()) {
            throw new RuntimeException("Jurusan tidak boleh kosong");
        }
        if (request.alamat() == null || request.alamat().isEmpty()) {
            throw new RuntimeException("Alamat tidak boleh kosong");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isEmpty()) {
            throw new RuntimeException("Nomor HP tidak boleh kosong");
        }
    }
}
