package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.BukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.BukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BukuServiceImpl implements BukuService {

    private final JwtUtil jwtUtil;
    private final BukuRepository bukuRepository;
    private final KategoriBukuRepository kategoriBukuRepository;
    private final AdminRepository adminRepository;
    private final BukuMapper bukuMapper;

    @Override
    public Buku addBukuBaru(BukuRequestRecord request, String token) {
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
                .orElseThrow(() -> new EntityNotFoundException("User dengan username " + username + " tidak ditemukan."));

        validasiMandatory(request);

        KategoriBuku kategoriBuku = kategoriBukuRepository.findById(request.kategoriBukuId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori Buku dengan ID " + request.kategoriBukuId() + " tidak ditemukan"));

        Buku buku = bukuMapper.requestToEntity(request);

        buku.setJudulBuku(request.judulBuku());
        buku.setPenulis(request.penulis());
        buku.setPenerbit(request.penerbit());
        buku.setTahunTerbit(request.tahunTerbit());
        buku.setJumlahSalinan(request.jumlahSalinan());
        buku.setLokasiRak(request.lokasiRak());
        buku.setNamaKategori(kategoriBuku.getNamaKategoriBuku());
        buku.setKategoriBukuId(kategoriBuku);
        buku.setStatusBuku(request.statusBuku());

        buku.setAdmin(admin);

        Buku savedBuku = bukuRepository.save(buku);
        bukuRepository.save(savedBuku);
        adminRepository.save(admin);

        return savedBuku;
    }

    @Override
    public Buku editBuku(BukuRequestRecord request, String token) {
        return null;
    }

    @Override
    public Page<SimpleMap> findAllBuku(BukuFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Buku> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("judulBuku", filterRequest.judulBuku(), builder);
        FilterUtil.builderConditionNotBlankLike("penulis", filterRequest.penulis(), builder);
        FilterUtil.builderConditionNotBlankLike("penerbit", filterRequest.penerbit(), builder);
        FilterUtil.builderConditionNotNullEqual("tahunTerbit", filterRequest.tahunTerbit(), builder);
        FilterUtil.builderConditionNotNullEqual("jumlahSalinan", filterRequest.jumlahSalinan(), builder);
        FilterUtil.builderConditionNotBlankLike("lokasiRak", filterRequest.lokasiRak(), builder);
        FilterUtil.builderConditionNotBlankLike("namaKategori", filterRequest.namaKategori(), builder);
        FilterUtil.builderConditionNotNullEqual("statusBuku", filterRequest.statusBuku(), builder);

        Page<Buku> listBuku = bukuRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listBuku.stream().map(buku -> {
            SimpleMap data = new SimpleMap();
            data.put("id", buku.getId());
            data.put("judulBuku", buku.getJudulBuku());
            data.put("penulis", buku.getPenulis());
            data.put("penerbit", buku.getPenerbit());
            data.put("tahunTerbit", buku.getTahunTerbit());
            data.put("jumlahSalinan", buku.getJumlahSalinan());
            data.put("lokasiRak", buku.getLokasiRak());
            data.put("namaKategoriBuku", buku.getNamaKategori());
            data.put("statusBuku", buku.getStatusBuku());
            return data;
        }).toList();



        return AppPage.create(listData, pageable, listBuku.getTotalElements());
    }

    @Override
    public SimpleMap findByIdBuku(String id) {
        return null;
    }

    @Override
    public void deleteByIdBuku(String id) {

    }

    private void validasiMandatory(BukuRequestRecord request) {
        if (request.judulBuku() == null || request.judulBuku().isEmpty()) {
            throw new RuntimeException("Judul Buku tidak boleh kosong");
        }
        if (request.penulis() == null || request.penulis().isEmpty()) {
            throw new RuntimeException("Penulis tidak boleh kosong");
        }
        if (request.penerbit() == null || request.penerbit().isEmpty()) {
            throw new RuntimeException("Penerbit tidak boleh kosong");
        }
        if (request.tahunTerbit() == null) {
            throw new RuntimeException("Tahun Terbit tidak boleh kosong");
        }
        if (request.jumlahSalinan() == null) {
            throw new RuntimeException("Tahun Terbit tidak boleh kosong");
        }
    }
}
