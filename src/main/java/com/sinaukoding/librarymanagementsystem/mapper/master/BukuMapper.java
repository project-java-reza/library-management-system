package com.sinaukoding.librarymanagementsystem.mapper.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class BukuMapper {
    public Buku requestToEntity(CreateBukuRequestRecord request) {
        Buku buku = Buku.builder()
                .judulBuku(request.judulBuku())
                .penulis(request.penulis())
                .penerbit(request.penerbit())
                .tahunTerbit(request.tahunTerbit())
                .jumlahSalinan(request.jumlahSalinan())
                .isbn(request.isbn())
                .deskripsi(request.deskripsi())
                .build();
        return buku;
    }
}
