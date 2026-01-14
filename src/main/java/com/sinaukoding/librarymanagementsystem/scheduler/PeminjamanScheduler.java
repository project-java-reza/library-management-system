package com.sinaukoding.librarymanagementsystem.scheduler;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PeminjamanScheduler {

    private final PeminjamanBukuRepository peminjamanBukuRepository;

    /**
     * Scheduled task yang berjalan setiap jam 00:00 setiap hari
     * untuk mengecek peminjaman yang tanggal kembalinya melewati 7 hari
     * dari tanggal pinjam dan mengubah statusnya menjadi DENDA
     *
     * Logika: Status berubah menjadi DENDA jika tanggalKembali > tanggalPinjam + 7 hari
     * Contoh: Jika tanggalPinjam = 14/1/2026 dan tanggalKembali = 23/1/2026,
     *         maka status akan berubah menjadi DENDA karena 23/1 > 21/1 (14+7)
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkOverduePeminjaman() {
        try {
            LocalDate today = LocalDate.now();

            // Cari semua peminjaman dengan status DIPINJAM yang tanggalKembalinya
            // sudah melewati lebih dari 7 hari dari tanggalPinjam
            List<PeminjamanBuku> overduePeminjaman = peminjamanBukuRepository.findAll().stream()
                    .filter(peminjaman -> {
                        // Hitung batas maksimal: tanggalPinjam + 7 hari
                        LocalDate batasMaksimal = peminjaman.getTanggalPinjam().plusDays(7);

                        // Status berubah menjadi DENDA jika:
                        // 1. Status masih DIPINJAM
                        // 2. tanggalKembali > tanggalPinjam + 7 hari
                        //    (artinya durasi peminjaman melebihi 7 hari)
                        return peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM &&
                               peminjaman.getTanggalKembali().isAfter(batasMaksimal);
                    })
                    .toList();

            if (!overduePeminjaman.isEmpty()) {
                int updatedCount = 0;
                for (PeminjamanBuku peminjaman : overduePeminjaman) {
                    try {
                        peminjaman.setStatusBukuPinjaman(StatusBukuPinjaman.DENDA);
                        peminjamanBukuRepository.save(peminjaman);
                        updatedCount++;
                    } catch (Exception e) {
                        // Silently ignore individual errors
                    }
                }
            }

        } catch (Exception e) {
            // Silently ignore global errors
        }
    }

    /**
     * Manual trigger method untuk testing atau menjalankan on-demand
     * Bisa dipanggil melalui endpoint: POST /api/admin/peminjaman/memeriksa-tenggat-pengembalian
     *
     * Logika: Status berubah menjadi DENDA jika tanggalKembali > tanggalPinjam + 7 hari
     */
    @Transactional
    public void checkOverduePeminjamanManual() {
        checkOverduePeminjaman();
    }
}
