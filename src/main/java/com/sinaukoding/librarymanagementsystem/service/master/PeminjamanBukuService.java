package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PeminjamanBukuService {
    PeminjamanBuku addPeminjamanBuku(PeminjamanBukuRequestRecord request, String token);

    Page<SimpleMap> findAllPeminjamanBuku(PeminjamanBukuFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdPeminjamanMahasiswa(String id);

    void deleteByIdPeminjamanMahasiswaSelesai(String id);
}
