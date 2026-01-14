package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.helper.LokasiRakHelper;
import com.sinaukoding.librarymanagementsystem.mapper.master.BukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.model.request.BukuSearchRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateBukuRequestRecord;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public Buku addBukuBaru(CreateBukuRequestRecord request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna Dengan " + username + " tidak ditemukan."));

        StatusBuku statusBuku = statusBukuService.getOrSave(EStatusBuku.TERSEDIA);

        KategoriBuku kategoriBuku = kategoriBukuRepository.findById(request.kategoriId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori Buku dengan ID " + request.kategoriId() + " tidak ditemukan"));

        Buku buku = bukuMapper.requestToEntity(request);

        buku.setJudulBuku(request.judulBuku());
        buku.setPenulis(request.penulis());
        buku.setPenerbit(request.penerbit());
        buku.setTahunTerbit(request.tahunTerbit());
        buku.setJumlahSalinan(request.jumlahSalinan());
        buku.setIsbn(request.isbn());
        buku.setDeskripsi(request.deskripsi());

        // Simpan lokasi rak: format combined dan 5 field terpisah
        String lokasiRakCombined = LokasiRakHelper.combineLokasiRak(
                request.lantai(), request.ruang(), request.rak(), request.nomorRak(), request.nomorBaris());
        buku.setLokasiRak(lokasiRakCombined);
        buku.setLantai(request.lantai());
        buku.setRuang(request.ruang());
        buku.setRak(request.rak());
        buku.setNomorRak(request.nomorRak());
        buku.setNomorBaris(request.nomorBaris());

        buku.setNamaKategori(kategoriBuku.getNamaKategoriBuku());
        buku.setKategoriBukuId(kategoriBuku);
        buku.setStatusBukuTersedia(statusBuku);

        buku.setAdmin(admin);

        Buku simpanBuku = bukuRepository.save(buku);

        return simpanBuku;
    }

    @Override
    public Buku editBuku(UpdateBukuRequestRecord request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        // Cari buku berdasarkan ID dari request
        Buku buku = bukuRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Buku dengan ID " + request.id() + " tidak ditemukan"));

        // Update field hanya jika tidak null (optional update)
        if (request.judulBuku() != null) {
            buku.setJudulBuku(request.judulBuku());
        }
        if (request.penulis() != null) {
            buku.setPenulis(request.penulis());
        }
        if (request.penerbit() != null) {
            buku.setPenerbit(request.penerbit());
        }
        if (request.tahunTerbit() != null) {
            buku.setTahunTerbit(request.tahunTerbit());
        }
        if (request.isbn() != null) {
            buku.setIsbn(request.isbn());
        }
        if (request.kategoriId() != null) {
            KategoriBuku kategoriBuku = kategoriBukuRepository.findById(request.kategoriId())
                    .orElseThrow(() -> new EntityNotFoundException("Kategori Buku dengan ID " + request.kategoriId() + " tidak ditemukan"));
            buku.setKategoriBukuId(kategoriBuku);
            buku.setNamaKategori(kategoriBuku.getNamaKategoriBuku());
        }
        if (request.jumlahSalinan() != null) {
            buku.setJumlahSalinan(request.jumlahSalinan());
        }
        if (request.deskripsi() != null) {
            buku.setDeskripsi(request.deskripsi());
        }

        // Update lokasi rak jika semua 5 field lokasi diisi
        if (LokasiRakHelper.isAllLokasiFieldsFilled(
                request.lantai(), request.ruang(), request.rak(),
                request.nomorRak(), request.nomorBaris())) {
            String lokasiRakCombined = LokasiRakHelper.combineLokasiRak(
                    request.lantai(), request.ruang(), request.rak(), request.nomorRak(), request.nomorBaris());
            buku.setLokasiRak(lokasiRakCombined);
            buku.setLantai(request.lantai());
            buku.setRuang(request.ruang());
            buku.setRak(request.rak());
            buku.setNomorRak(request.nomorRak());
            buku.setNomorBaris(request.nomorBaris());
        }

        if (request.statusBuku() != null) {
            StatusBuku statusBuku = statusBukuRepository.findByStatusBuku(request.statusBuku())
                    .orElseThrow(() -> new RuntimeException("Status Buku " + request.statusBuku() + " tidak ditemukan"));
            buku.setStatusBukuTersedia(statusBuku);
        }

        buku.setAdmin(admin);
        bukuRepository.save(buku);
        return buku;
    }

    @Override
    public Page<SimpleMap> findAllBuku(BukuSearchRequestRecord request) {
        // STEP 1: Normalize & Validate Sort Parameters
        String validSorts = "id,judulBuku,penulis,penerbit,tahunTerbit,jumlahSalinan,lokasiRak,namaKategori,createdDate,modifiedDate";

        String sortColumn = request.sortColumn() != null ? request.sortColumn() : "createdDate";
        String sortDirection = request.sortColumnDir() != null ? request.sortColumnDir().toUpperCase() : "DESC";

        // Validate sort column
        if (!validSorts.contains(sortColumn)) {
            sortColumn = "createdDate";
        }

        // Validate sort direction
        if (!sortDirection.equals("ASC") && !sortDirection.equals("DESC")) {
            sortDirection = "DESC";
        }

        boolean isAscending = sortDirection.equals("ASC");

        // STEP 2: Create Pageable from request parameters
        int pageNumber = request.pageNumber() != null ? request.pageNumber() - 1 : 0; // Spring Data pages start from 0
        int pageSize = request.pageSize() != null ? request.pageSize() : 10;

        Sort sort = Sort.by(
                isAscending ? Sort.Direction.ASC : Sort.Direction.DESC,
                sortColumn
        );

        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        System.out.println("Pageable sort: " + pageable.getSort());

        // STEP 3: Build Query with Filters
        CustomBuilder<Buku> builder = new CustomBuilder<>();

        // Global search - search in judul, penulis, or penerbit
        if (request.search() != null && !request.search().isBlank()) {
            FilterUtil.builderConditionSearchInOr("judulBuku", "penulis", "penerbit", request.search(), builder);
        } else {
            // Individual field searches
            FilterUtil.builderConditionNotBlankLike("judulBuku", request.judulBuku(), builder);
            FilterUtil.builderConditionNotBlankLike("penulis", request.penulis(), builder);
            FilterUtil.builderConditionNotBlankLike("penerbit", request.penerbit(), builder);
        }

        // STEP 4: Execute Query
        Page<Buku> listBuku = bukuRepository.findAll(builder.build(), pageable);

        // STEP 5: Calculate Numbering Variables
        int recordsTotal = (int) listBuku.getTotalElements();
        int recordsBeforeCurrentPage = (int) pageable.getOffset();
        boolean isDescending = !isAscending;

        // STEP 6: Map Data with Dynamic Numbering
        List<SimpleMap> listData = new java.util.ArrayList<>();
        int index = 0;

        for (Buku buku : listBuku.getContent()) {
            SimpleMap data = new SimpleMap();

            // Dynamic numbering berdasarkan sort direction
            int nomor;
            if (isDescending) {
                // DESC: Nomor mundur dari total
                nomor = recordsTotal - recordsBeforeCurrentPage - index;
            } else {
                // ASC: Nomor maju dari awal
                nomor = recordsBeforeCurrentPage + index + 1;
            }

            System.out.println("Index: " + index + ", Nomor: " + nomor + ", Judul: " + buku.getJudulBuku());

            data.put("no", nomor);
            data.put("id", buku.getId());
            data.put("judulBuku", buku.getJudulBuku());
            data.put("penulis", buku.getPenulis());
            data.put("penerbit", buku.getPenerbit());
            data.put("tahunTerbit", buku.getTahunTerbit());
            data.put("jumlahSalinan", buku.getJumlahSalinan());
            data.put("isbn", buku.getIsbn());
            data.put("deskripsi", buku.getDeskripsi());

            // Lokasi rak - ambil dari field terpisah (prioritas) atau parse dari string combined
            if (LokasiRakHelper.isAllLokasiFieldsFilled(
                    buku.getLantai(), buku.getRuang(), buku.getRak(),
                    buku.getNomorRak(), buku.getNomorBaris())) {
                // Gunakan field terpisah dari database
                data.put("lantai", buku.getLantai());
                data.put("ruang", buku.getRuang());
                data.put("rak", buku.getRak());
                data.put("nomorRak", buku.getNomorRak());
                data.put("nomorBaris", buku.getNomorBaris());
            } else {
                // Parse dari string combined untuk data lama
                SimpleMap lokasi = LokasiRakHelper.parseLokasiRak(buku.getLokasiRak());
                data.put("lantai", lokasi.get("lantai"));
                data.put("ruang", lokasi.get("ruang"));
                data.put("rak", lokasi.get("rak"));
                data.put("nomorRak", lokasi.get("nomorRak"));
                data.put("nomorBaris", lokasi.get("nomorBaris"));
            }

            data.put("namaKategoriBuku", buku.getNamaKategori());
            data.put("statusBuku", buku.getStatusBukuTersedia());
            listData.add(data);
            index++;
        }

        return AppPage.create(listData, pageable, recordsTotal);
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
        data.put("isbn", buku.getIsbn());
        data.put("deskripsi", buku.getDeskripsi());

        // Lokasi rak - ambil dari field terpisah (prioritas) atau parse dari string combined
        if (LokasiRakHelper.isAllLokasiFieldsFilled(
                buku.getLantai(), buku.getRuang(), buku.getRak(),
                buku.getNomorRak(), buku.getNomorBaris())) {
            // Gunakan field terpisah dari database
            data.put("lantai", buku.getLantai());
            data.put("ruang", buku.getRuang());
            data.put("rak", buku.getRak());
            data.put("nomorRak", buku.getNomorRak());
            data.put("nomorBaris", buku.getNomorBaris());
        } else {
            // Parse dari string combined untuk data lama
            SimpleMap lokasi = LokasiRakHelper.parseLokasiRak(buku.getLokasiRak());
            data.put("lantai", lokasi.get("lantai"));
            data.put("ruang", lokasi.get("ruang"));
            data.put("rak", lokasi.get("rak"));
            data.put("nomorRak", lokasi.get("nomorRak"));
            data.put("nomorBaris", lokasi.get("nomorBaris"));
        }

        data.put("namaKategoriBuku", buku.getNamaKategori());
        data.put("statusBuku", buku.getStatusBukuTersedia());
        return data;
    }

    @Override
    public SimpleMap getStatusBuku(String id) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("bukuId", buku.getId());
        data.put("judulBuku", buku.getJudulBuku());
        data.put("jumlahSalinan", buku.getJumlahSalinan());
        data.put("statusTersedia", buku.getStatusBukuTersedia().getStatusBuku().name());

        return data;
    }

    @Override
    public void deleteByIdBuku(String id) {
        var buku = bukuRepository.findById(id).orElseThrow(() -> new RuntimeException("Data Buku tidak ditemukan"));
        bukuRepository.deleteById(id);
    }
}
