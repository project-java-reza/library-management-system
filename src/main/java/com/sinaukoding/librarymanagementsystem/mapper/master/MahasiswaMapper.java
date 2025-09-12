package com.sinaukoding.librarymanagementsystem.mapper.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class MahasiswaMapper {
    public Mahasiswa requestToEntity(MahasiswaRequestRecord request) {
        Mahasiswa mahasiswa = Mahasiswa.builder()
                .nim(request.nim())
                .jurusan(request.jurusan().toUpperCase())
                .alamat(request.alamat().toUpperCase())
                .phoneNumber(request.phoneNumber())
                .build();
        return mahasiswa;
    }
}
