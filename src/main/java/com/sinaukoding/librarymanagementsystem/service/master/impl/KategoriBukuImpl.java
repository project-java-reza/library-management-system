package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.KategoriBukuMapper;
import com.sinaukoding.librarymanagementsystem.model.enums.KategoriBukuEnum;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KategoriBukuImpl implements KategoriBukuService {


    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final KategoriBukuMapper kategoriBukuMapper;
    private final KategoriBukuRepository kategoriBukuRepository;

    @Override
    public KategoriBuku addKategoriBuku(KategoriBukuRequestRecord request, String token) {
        // Membersihkan prefix Bearer
        String prefixBearerToken = token;
        if (prefixBearerToken != null && prefixBearerToken.startsWith("Bearer ")) {
            prefixBearerToken = prefixBearerToken.substring(7);
        }

        // mengambil username dari JWT
        String username = jwtUtil.extractUsername(prefixBearerToken);
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username kosong atau tidak valid.");
        }

        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        KategoriBuku kategoriBuku = kategoriBukuMapper.requestToEntity(request);
        kategoriBuku.setNamaKategoriBuku(request.namaKategoriBuku());
        kategoriBuku.setDeskripsiKategori(request.deskripsiKategori());

        validasiMandatory(request);

        KategoriBuku saved = kategoriBukuRepository.save(kategoriBuku);
        return saved;
    }

    private void validasiMandatory(KategoriBukuRequestRecord request) {
        if (request.deskripsiKategori() == null || request.deskripsiKategori().isEmpty()) {
            throw new RuntimeException("Deskripsi Buku tidak boleh kosong");
        }

    }
}
