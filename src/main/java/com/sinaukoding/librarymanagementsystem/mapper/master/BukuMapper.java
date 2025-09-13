package com.sinaukoding.librarymanagementsystem.mapper.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class BukuMapper {
    public Buku requestToEntity(BukuRequestRecord request) {
        Buku buku = Buku.builder()
                .judulBuku(request.judulBuku())
                .penulis(request.penulis())
                .penerbit(request.penerbit())
                .tahunTerbit(request.tahunTerbit())
                .jumlahSalinan(request.jumlahSalinan())
                .build();
        return buku;
    }
}
