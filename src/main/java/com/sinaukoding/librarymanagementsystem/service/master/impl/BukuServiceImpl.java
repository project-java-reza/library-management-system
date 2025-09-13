package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.BukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.model.filter.BukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.StatusBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.StatusBukuService;
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
    private final StatusBukuService statusBukuService;
    private final StatusBukuRepository statusBukuRepository;

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
                .orElseThrow(() -> new EntityNotFoundException("Pengguna Dengan " + username + " tidak ditemukan."));

        validasiMandatory(request);


        StatusBuku statusBuku = statusBukuService.getOrSave(EStatusBuku.TERSEDIA);

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
        buku.setStatusBukuTersedia(statusBuku);

        buku.setAdmin(admin);

        Buku savedBuku = bukuRepository.save(buku);
        bukuRepository.save(savedBuku);
        adminRepository.save(admin);

        return savedBuku;
    }

    @Override
    public Buku editBuku(BukuRequestRecord request, String token) {
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

        // validasi mandatory
        validasiMandatory(request);

        // validasi data existing
        if(bukuRepository.existsByLokasiRak(request.lokasiRak())) {
            throw new RuntimeException("Lokasi Rak [" + request.lokasiRak() + "] sudah ada");
        }

        Buku buku = bukuRepository.findByAdmin(admin)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan"));

        buku.setJudulBuku(request.judulBuku());
        buku.setPenulis(request.penulis());
        buku.setPenerbit(request.penerbit());
        buku.setTahunTerbit(request.tahunTerbit());
        buku.setJumlahSalinan(request.jumlahSalinan());
        buku.setLokasiRak(request.lokasiRak());

        // Mengambil StatusBuku dari EStatusBuku
        if (request.statusBuku() != null) {
            // Cari entitas StatusBuku berdasarkan nilai EStatusBuku
            StatusBuku statusBuku = statusBukuRepository.findByStatusBuku(request.statusBuku())
                    .orElseThrow(() -> new RuntimeException("Status Buku " + request.statusBuku() + " tidak ditemukan"));

            // Update statusBuku entitas Buku
            buku.setStatusBukuTersedia(statusBuku);  // Menetapkan StatusBuku yang ditemukan
        }


        bukuRepository.save(buku);
        return buku;
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
        FilterUtil.builderConditionNotNullEqual("statusBuku", filterRequest.EStatusBuku(), builder);

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
            data.put("statusBuku", buku.getStatusBukuTersedia());
            return data;
        }).toList();
        return AppPage.create(listData, pageable, listBuku.getTotalElements());
    }

    @Override
    public SimpleMap findByIdBuku(String id) {
        var buku = bukuRepository.findById(id).orElseThrow(() -> new RuntimeException("Data Buku tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", buku.getId());
        data.put("judulBuku", buku.getJudulBuku());
        data.put("penulis", buku.getPenulis());
        data.put("penerbit", buku.getPenerbit());
        data.put("tahunTerbit", buku.getTahunTerbit());
        data.put("jumlahSalinan", buku.getJumlahSalinan());
        data.put("lokasiRak", buku.getLokasiRak());
        data.put("namaKategoriBuku", buku.getNamaKategori());
        data.put("statusBuku", buku.getStatusBukuTersedia());
        return data;
    }

    @Override
    public void deleteByIdBuku(String id) {
        var buku = bukuRepository.findById(id).orElseThrow(() -> new RuntimeException("Data Buku tidak ditemukan"));
        bukuRepository.deleteById(id);
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
