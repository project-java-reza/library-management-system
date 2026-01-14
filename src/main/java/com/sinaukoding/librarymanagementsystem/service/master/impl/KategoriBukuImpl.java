package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.KategoriBukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.KategoriBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchKategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KategoriBukuImpl implements KategoriBukuService {


    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final KategoriBukuMapper kategoriBukuMapper;
    private final KategoriBukuRepository kategoriBukuRepository;
    private final BukuRepository bukuRepository;

    @Override
    public KategoriBuku addKategoriBuku(KategoriBukuRequestRecord request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        KategoriBuku kategoriBuku = kategoriBukuMapper.requestToEntity(request);
        kategoriBuku.setNamaKategoriBuku(request.nama());
        kategoriBuku.setDeskripsiKategori(request.deskripsi());
        kategoriBuku.setAdmin(admin);

        KategoriBuku simpanKategoriBuku = kategoriBukuRepository.save(kategoriBuku);
        return simpanKategoriBuku;
    }

    @Override
    public KategoriBuku editKategoriBuku(KategoriBukuRequestRecord request, String username) {
        Admin admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        // Cek kategori buku by ID
        KategoriBuku kategoriBuku = kategoriBukuRepository.findById(request.id())
                .orElseThrow(() -> new EntityNotFoundException("Data Kategori Buku tidak ditemukan"));

        // Cek nama kategori buku unik (kecuali untuk record yang sama)
        if(kategoriBukuRepository.existsByNamaKategoriBukuAndIdNot(request.nama(), request.id())) {
            throw new RuntimeException("Nama Kategori Buku [" + request.nama() + "] sudah digunakan");
        }

        kategoriBuku.setNamaKategoriBuku(request.nama());
        kategoriBuku.setDeskripsiKategori(request.deskripsi());
        kategoriBuku.setAdmin(admin);
        kategoriBukuRepository.save(kategoriBuku);
        return kategoriBuku;
    }

    @Override
    public Page<SimpleMap> findAllKategoriBuku(SearchKategoriBukuRequestRecord searchRequest) {
        // Map sort column from request to entity property name
        String entitySortColumn = mapSortColumn(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "modifiedDate");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        // Build filter from search parameter
        KategoriBukuFilterRecord filterRequest = new KategoriBukuFilterRecord(
                searchRequest.search(),  // Use search parameter for filtering by nama kategori
                null                    // deskripsiKategori not used in search
        );

        CustomBuilder<KategoriBuku> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("namaKategoriBuku", filterRequest.namaKategoriBuku(), builder);
        FilterUtil.builderConditionNotBlankLike("deskripsiKategori", filterRequest.deskripsiKategori(), builder);

        Page<KategoriBuku> listKategoriBuku = kategoriBukuRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listKategoriBuku.stream().map(kategoriBuku->{
            SimpleMap data = new SimpleMap();
            data.put("id", kategoriBuku.getId());
            data.put("nama", kategoriBuku.getNamaKategoriBuku());
            data.put("deskripsi", kategoriBuku.getDeskripsiKategori());
            return data;
        }).toList();
        return AppPage.create(listData, pageable, listKategoriBuku.getTotalElements());
    }

    private String mapSortColumn(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "modifiedDate";
        }
        // Map frontend column names to entity property names
        return switch (sortColumn) {
            case "nama" -> "nama";
            case "modifiedDate" -> "modifiedDate";
            case "createdDate" -> "createdDate";
            default -> "modifiedDate";
        };
    }

    @Override
    public SimpleMap findByIdKategoriBuku(String id) {
        var kategoriBuku = kategoriBukuRepository.findById(id).orElseThrow(()-> new RuntimeException("Data Kategori Buku tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", kategoriBuku.getId());
        data.put("nama", kategoriBuku.getNamaKategoriBuku());
        data.put("deskripsi", kategoriBuku.getDeskripsiKategori());
        return data;
    }

    @Override
    public void deleteKategoriBuku(String id) {
        var kategoriBuku = kategoriBukuRepository.findById(id).orElseThrow(()-> new RuntimeException("Data Kategori Buku tidak ditemukan"));
        kategoriBukuRepository.deleteById(id);
    }
}
