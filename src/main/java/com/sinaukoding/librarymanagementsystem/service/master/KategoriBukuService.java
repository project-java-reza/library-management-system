package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchKategoriBukuRequestRecord;
import org.springframework.data.domain.Page;

import java.util.List;

public interface KategoriBukuService {
    KategoriBuku addKategoriBuku(KategoriBukuRequestRecord request, String username);

    KategoriBuku editKategoriBuku(KategoriBukuRequestRecord request, String username);

    Page<SimpleMap> findAllKategoriBuku(SearchKategoriBukuRequestRecord searchRequest);

    SimpleMap findByIdKategoriBuku(String id);

    void deleteKategoriBuku(String id);
}
