package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.KategoriBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface KategoriBukuService {
    KategoriBuku addKategoriBuku(KategoriBukuRequestRecord request, String token);

    KategoriBuku editKategoriBuku(KategoriBukuRequestRecord request, String token);

    Page<SimpleMap> findAllKategoriBuku(KategoriBukuFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdKategoriBuku(String id);

    void deleteKategoriBuku(String id);
}
