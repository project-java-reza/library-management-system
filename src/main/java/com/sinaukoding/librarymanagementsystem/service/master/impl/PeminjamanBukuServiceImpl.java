package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.PeminjamanBukuMapper;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PeminjamanBukuServiceImpl implements PeminjamanBukuService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PeminjamanBukuRepository peminjamanBukuRepository;
    private final BukuRepository bukuRepository;
    private final PeminjamanBukuMapper peminjamanBukuMapper;

    @Override
    public PeminjamanBuku addPeminjamanBuku(PeminjamanBukuRequestRecord request, String token) {
        // Membersihkan prefix Bearer
        String prefixBearerToken = token;
        if (prefixBearerToken != null && prefixBearerToken.startsWith("Bearer ")) {
            prefixBearerToken = prefixBearerToken.substring(7);
        }

        // mengambil username dari JWT
        String username = jwtUtil.extractUsername(prefixBearerToken);
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username kosong atau tidak valid.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna Dengan " + username + " tidak ditemukan."));

        // validasi mandatory
        validasiMandatory(request);

        Buku buku = bukuRepository.findById(request.bukuId())
                .orElseThrow(() -> new EntityNotFoundException("Data Buku tidak ditemukan"));

        // mengecek ketersediaan buku
        if (buku.getJumlahSalinan() <= 0) {
            throw new RuntimeException("Buku tidak tersedia, jumlah salinan buku habis.");
        }

        // mengurangi jumlah salinan buku
        buku.setJumlahSalinan(buku.getJumlahSalinan() - 1);
        bukuRepository.save(buku);

        PeminjamanBuku peminjamanBuku = peminjamanBukuMapper.requestToEntity(request);

        peminjamanBuku.setTanggalPinjam(request.tanggalPinjam());
        peminjamanBuku.setTanggalKembali(request.tanggalKembali());
        peminjamanBuku.setStatusBukuPinjaman(request.statusBukuPinjaman());

        peminjamanBuku.setBuku(buku);
        peminjamanBuku.setUser(user);

        PeminjamanBuku savedPeminjamanBuku = peminjamanBukuRepository.save(peminjamanBuku);
        peminjamanBukuRepository.save(savedPeminjamanBuku);
        userRepository.save(user);

        return savedPeminjamanBuku;
    }

    @Override
    public PeminjamanBuku editPeminjamanStatusBuku(PeminjamanBukuRequestRecord request, String token) {
        // Membersihkan prefix Bearer
        String prefixBearerToken = token;
        if (prefixBearerToken != null && prefixBearerToken.startsWith("Bearer ")) {
            prefixBearerToken = prefixBearerToken.substring(7);
        }

        // mengambil username dari JWT
        String username = jwtUtil.extractUsername(prefixBearerToken);
        if (username == null || username.isBlank()) {
            throw new BadCredentialsException("Username kosong atau tidak valid.");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna Dengan " + username + " tidak ditemukan."));

        // validasi mandatory
        validasiMandatory(request);

        PeminjamanBuku peminjamanBuku = peminjamanBukuRepository.findByUser(user)
                .orElseThrow(()-> new EntityNotFoundException("Data Peminjaman Buku tidak ditemukan"));

        peminjamanBuku.setStatusBukuPinjaman(request.statusBukuPinjaman());
        peminjamanBukuRepository.save(peminjamanBuku);
        return peminjamanBuku;
    }

    @Override
    public Page<SimpleMap> findAllPeminjamanBuku(PeminjamanBukuFilterRecord filterRequest, Pageable pageable) {
        return null;
    }

    @Override
    public SimpleMap findByIdPeminjamanMahasiswa(String id) {
        return null;
    }

    @Override
    public void deleteByIdPeminjamanMahasiswaSelesai(String id) {

    }

    private void validasiMandatory(PeminjamanBukuRequestRecord request) {
        if (request.statusBukuPinjaman() == null) {
            throw new RuntimeException("Status Buku tidak boleh kosong");
        }
    }
}
