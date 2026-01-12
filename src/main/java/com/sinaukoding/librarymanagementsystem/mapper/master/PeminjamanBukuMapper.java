package com.sinaukoding.librarymanagementsystem.mapper.master;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class PeminjamanBukuMapper {
    public PeminjamanBuku requestToEntity(PeminjamanBukuRequestRecord request) {
        PeminjamanBuku peminjamanBuku = PeminjamanBuku.builder()
                .tanggalPinjam(request.tanggalPinjam())
                .tanggalKembali(request.tanggalKembali())
                .statusBukuPinjaman(request.statusBukuPinjaman())
                .denda(request.denda() != null ? request.denda() : 0L)
                .build();
        return peminjamanBuku;
    }

}
