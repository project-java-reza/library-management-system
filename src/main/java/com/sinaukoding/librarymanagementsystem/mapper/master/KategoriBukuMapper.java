package com.sinaukoding.librarymanagementsystem.mapper.master;

import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class KategoriBukuMapper {
    public KategoriBuku requestToEntity(KategoriBukuRequestRecord request) {
        KategoriBuku kategoriBuku = KategoriBuku.builder()
                .deskripsiKategori(request.deskripsi())
                .build();
        return kategoriBuku;
    }
}
