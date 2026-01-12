package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreatePeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchRecentPeminjamanRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdatePeminjamanBukuRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeminjamanBukuService {
    PeminjamanBuku addPeminjamanBuku(PeminjamanBukuRequestRecord request, String token);

    PeminjamanBuku addPeminjamanBuku(CreatePeminjamanBukuRequestRecord request, String token);

    Page<SimpleMap> findAllPeminjamanBuku(PeminjamanBukuFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdPeminjamanMahasiswa(String id);

    void deleteByIdPeminjamanMahasiswaSelesai(String id);

    SimpleMap updatePeminjamanBuku(String id, PeminjamanBukuRequestRecord request);

    SimpleMap updatePeminjamanBuku(String id, UpdatePeminjamanBukuRequestRecord request);

    // New methods for user operations
    Page<SimpleMap> findPeminjamanByUser(String username, PeminjamanBukuFilterRecord filterRequest, Pageable pageable);

    void returnBuku(String peminjamanId);

    void approveReturnBuku(String peminjamanId);

    // Recent peminjaman with request-based pagination
    Page<SimpleMap> getRecentPeminjaman(SearchRecentPeminjamanRequestRecord searchRequest);
}
