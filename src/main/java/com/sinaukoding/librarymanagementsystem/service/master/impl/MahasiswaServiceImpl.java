package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.master.MahasiswaMapper;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MahasiswaServiceImpl implements MahasiswaService {

    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final MahasiswaMapper mahasiswaMapper;
    private final MahasiswaRepository mahasiswaRepository;

    @Override
    public Mahasiswa addProfileMahasiswaUser(MahasiswaRequestRecord request) {
        // validasi mandatory
        validasiMandatory(request);

        var mahasiswa = mahasiswaMapper.requestToEntity(request);
        mahasiswa.setNim(request.nim());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());
        mahasiswaRepository.save(mahasiswa);
        return mahasiswa;
    }
//
//    @Override
//    public Mahasiswa editProfileMahasiswaUser(MahasiswaRequestRecord request) {
//        return null;
//    }
//
//    @Override
//    public Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaRequestRecord filterRequest, Pageable pageable) {
//        return null;
//    }
//
//    @Override
//    public SimpleMap findByIdMahasiswaUser(String id) {
//        return null;
//    }
//
//    @Override
//    public void deleteByIdMahasiswaUser(String id) {
//
//    }
//
    private void validasiMandatory(MahasiswaRequestRecord request) {
        if (request.nim() == null || request.nim().isEmpty()) {
            throw new RuntimeException("NIM tidak boleh kosong");
        }
        if (request.jurusan() == null || request.jurusan().isEmpty()) {
            throw new RuntimeException("Jurusan tidak boleh kosong");
        }
        if (request.alamat() == null || request.alamat().isEmpty()) {
            throw new RuntimeException("Alamat tidak boleh kosong");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isEmpty()) {
            throw new RuntimeException("Nomor HP tidak boleh kosong");
        }
    }
}
