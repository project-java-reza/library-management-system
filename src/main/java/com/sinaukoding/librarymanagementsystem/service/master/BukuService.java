package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.BukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface BukuService {
    Buku addBukuBaru(BukuRequestRecord request, String token);

    Buku editBuku(BukuRequestRecord request, String token);

    Page<SimpleMap> findAllBuku(BukuFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdBuku(String id);

    void deleteByIdBuku(String id);

}
