package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.master.MahasiswaMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MahasiswaServiceImpl implements MahasiswaService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MahasiswaMapper mahasiswaMapper;
    private final MahasiswaRepository mahasiswaRepository;

    @Override
    public Mahasiswa addProfileMahasiswaUser(MahasiswaRequestRecord request, String token) {

        // Membersihkan prefix Bearer
        String preBearerToken = token;
        if (preBearerToken != null && preBearerToken.startsWith("Bearer ")) {
            preBearerToken = preBearerToken.substring(7);
        }

        // mengambil username dari JWT
        String username = jwtUtil.extractUsername(preBearerToken);
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username kosong atau tidak valid.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User dengan username " + username + " tidak ditemukan."));

        validasiMandatory(request);

        Mahasiswa mahasiswa = mahasiswaMapper.requestToEntity(request);

        mahasiswa.setNim(request.nim());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());

        mahasiswa.setNama(user.getNama());
        mahasiswa.setUser(user);
        user.setMahasiswa(mahasiswa);

        Mahasiswa saved = mahasiswaRepository.save(mahasiswa);
        userRepository.save(user);

        return saved;
    }

    @Override
    public Mahasiswa editProfileMahasiswaUser(MahasiswaRequestRecord request) {
        return null;
    }

    @Override
    public Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaRequestRecord filterRequest, Pageable pageable) {
        CustomBuilder<Mahasiswa> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("nama", filterRequest.nama(), builder);
        FilterUtil.builderConditionNotBlankLike("nim", filterRequest.nim(), builder);
        FilterUtil.builderConditionNotBlankLike("jurusan", filterRequest.jurusan(), builder);
        FilterUtil.builderConditionNotBlankLike("alamat", filterRequest.alamat(), builder);
        FilterUtil.builderConditionNotBlankLike("phoneNumber", filterRequest.phoneNumber(), builder);

        Page<Mahasiswa> listUser = mahasiswaRepository.findAll(builder.build(), pageable);
        List<SimpleMap> listData = listUser.stream().map(user -> {
            SimpleMap data = new SimpleMap();
            data.put("id", user.getId());
            data.put("nama", user.getUser().getNama());
            data.put("nim", user.getNim());
            data.put("jurusan", user.getJurusan());
            data.put("alamat", user.getAlamat());
            data.put("phoneNumber", user.getPhoneNumber());
            return data;
        }).toList();

        return AppPage.create(listData, pageable, listUser.getTotalElements());
    }

    @Override
    public SimpleMap findByIdMahasiswa(String id) {
        var mahasiswa = mahasiswaRepository.findById(id).orElseThrow(() -> new RuntimeException("Data Mahasiswa tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("nama", mahasiswa.getUser().getNama());
        data.put("nim", mahasiswa.getNim());
        data.put("jurusan", mahasiswa.getJurusan());
        data.put("alamat", mahasiswa.getAlamat());
        data.put("phoneNumber", mahasiswa.getPhoneNumber());
        return data;
    }


        @Override
    public void deleteByIdMahasiswaUser(String id) {

    }

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
