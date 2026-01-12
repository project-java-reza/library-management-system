package com.sinaukoding.librarymanagementsystem.service.dashboard.impl;

import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final BukuRepository bukuRepository;
    private final KategoriBukuRepository kategoriBukuRepository;
    private final MahasiswaRepository mahasiswaRepository;
    private final PeminjamanBukuRepository peminjamanBukuRepository;

    @Override
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Total buku
        long totalBuku = bukuRepository.count();
        stats.put("totalBuku", totalBuku);

        // Total kategori
        long totalKategori = kategoriBukuRepository.count();
        stats.put("totalKategori", totalKategori);

        // Total mahasiswa
        long totalMahasiswa = mahasiswaRepository.count();
        stats.put("totalMahasiswa", totalMahasiswa);

        // Total peminjaman (all records)
        long totalPeminjaman = peminjamanBukuRepository.count();
        stats.put("totalPeminjaman", totalPeminjaman);

        // Total buku yang sedang dipinjam
        long bukuDipinjam = peminjamanBukuRepository.findAll().stream()
                .filter(p -> p.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM)
                .count();
        stats.put("bukuDipinjam", bukuDipinjam);

        // Total peminjaman aktif
        long totalPeminjamanAktif = peminjamanBukuRepository.findAll().stream()
                .filter(p -> p.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM)
                .count();
        stats.put("totalPeminjamanAktif", totalPeminjamanAktif);

        // Total peminjaman selesai
        long totalPeminjamanSelesai = peminjamanBukuRepository.findAll().stream()
                .filter(p -> p.getStatusBukuPinjaman() == StatusBukuPinjaman.SUDAH_DIKEMBALIKAN)
                .count();
        stats.put("totalPeminjamanSelesai", totalPeminjamanSelesai);

        // Buku yang terlambat dikembalikan
        long totalTerlambat = peminjamanBukuRepository.findAll().stream()
                .filter(p -> p.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM)
                .filter(p -> p.getTanggalKembali().isBefore(LocalDate.now()))
                .count();
        stats.put("totalTerlambat", totalTerlambat);

        return stats;
    }

    @Override
    public Page<SimpleMap> getRecentPeminjaman(Pageable pageable) {
        log.info("Getting recent peminjaman with pageable: {}", pageable);

        // Get all peminjaman with pagination
        Page<com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku> peminjamanPage =
            peminjamanBukuRepository.findAll(pageable);

        log.info("Found {} peminjaman records", peminjamanPage.getTotalElements());

        // Calculate Numbering Variables
        int recordsTotal = (int) peminjamanPage.getTotalElements();
        int recordsBeforeCurrentPage = (int) pageable.getOffset();
        int numberOfElementsInPage = peminjamanPage.getNumberOfElements();

        // Determine sort direction from pageable
        boolean isAscending = true;
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            isAscending = order.isAscending();
        }

        List<SimpleMap> listData = new ArrayList<>();
        int index = 0;

        for (com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku peminjaman : peminjamanPage.getContent()) {
            SimpleMap data = new SimpleMap();

            // Dynamic numbering berdasarkan sort direction
            int nomor;
            if (isAscending) {
                // ASC: Nomor maju dari awal halaman
                nomor = recordsBeforeCurrentPage + index + 1;
            } else {
                // DESC: Nomor mundur dari akhir halaman
                nomor = recordsBeforeCurrentPage + numberOfElementsInPage - index;
            }

            // Get data safely with fallback
            String nim = null;
            String namaMahasiswa = null;
            try {
                if (peminjaman.getUser() != null &&
                    peminjaman.getUser().getMahasiswa() != null) {
                    nim = peminjaman.getUser().getMahasiswa().getNim();
                    namaMahasiswa = peminjaman.getUser().getMahasiswa().getNama();
                }
            } catch (Exception e) {
                log.warn("Error getting mahasiswa data for peminjaman id: {}", peminjaman.getId(), e);
            }

            String judulBuku = null;
            try {
                if (peminjaman.getBuku() != null) {
                    judulBuku = peminjaman.getBuku().getJudulBuku();
                }
            } catch (Exception e) {
                log.warn("Error getting buku data for peminjaman id: {}", peminjaman.getId(), e);
            }

            data.put("no", nomor);
            data.put("nim", nim);
            data.put("namaMahasiswa", namaMahasiswa);
            data.put("judulBuku", judulBuku);
            data.put("tanggalPinjam", peminjaman.getTanggalPinjam());
            data.put("statusBukuPinjaman", peminjaman.getStatusBukuPinjaman());

            listData.add(data);
            index++;
        }

        return AppPage.create(listData, pageable, peminjamanPage.getTotalElements());
    }
}
