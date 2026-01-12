package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.MahasiswaFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateMahasiswaRequestRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MahasiswaService  {
    Mahasiswa addProfileMahasiswaUser(MahasiswaProfileRequestRecord request, String token);

    Mahasiswa editProfileMahasiswaUser(MahasiswaProfileRequestRecord request, String token);

    // Admin methods (without token)
    SimpleMap createMahasiswa(CreateMahasiswaRequestRecord request);

    SimpleMap updateMahasiswa(String id, UpdateMahasiswaRequestRecord request);

    Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaFilterRecord filterRequest, Pageable pageable);

    SimpleMap findByIdMahasiswa(String id);

    /**
     * Delete mahasiswa and user by userId
     * @param userId - User ID from m_user table
     */
    void deleteByIdMahasiswaUser(String userId);
}
