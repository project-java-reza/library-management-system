package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MahasiswaService  {
    Mahasiswa addProfileMahasiswaUser(MahasiswaRequestRecord request, String token);

    Mahasiswa editProfileMahasiswaUser(MahasiswaRequestRecord request);

    Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaRequestRecord filterRequest, Pageable pageable);

    SimpleMap findByIdMahasiswa(String id);

    void deleteByIdMahasiswaUser(String id);
}
