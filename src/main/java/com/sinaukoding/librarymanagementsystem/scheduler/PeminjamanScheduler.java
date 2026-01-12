package com.sinaukoding.librarymanagementsystem.scheduler;

import com.sinaukoding.librarymanagementsystem.entity.master.PeminjamanBuku;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PeminjamanScheduler {

    private final PeminjamanBukuRepository peminjamanBukuRepository;

    /**
     * Scheduled task yang berjalan setiap jam 00:00 setiap hari
     * untuk mengecek peminjaman yang melewati batas tanggal kembali
     * dan mengubah statusnya menjadi DENDA
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void checkOverduePeminjaman() {
        log.info("=== Starting scheduled check for overdue peminjaman ===");

        try {
            LocalDate today = LocalDate.now();

            // Cari semua peminjaman dengan status DIPINJAM yang sudah melewati tanggal kembali
            List<PeminjamanBuku> overduePeminjaman = peminjamanBukuRepository.findAll().stream()
                    .filter(peminjaman ->
                            peminjaman.getStatusBukuPinjaman() == StatusBukuPinjaman.DIPINJAM &&
                            peminjaman.getTanggalKembali().isBefore(today)
                    )
                    .toList();

            if (overduePeminjaman.isEmpty()) {
                log.info("No overdue peminjaman found");
            } else {
                log.info("Found {} overdue peminjaman, updating status to DENDA", overduePeminjaman.size());

                int updatedCount = 0;
                for (PeminjamanBuku peminjaman : overduePeminjaman) {
                    try {
                        log.info("Updating peminjaman ID {} - Book: {}, User: {}, Due Date: {}",
                                peminjaman.getId(),
                                peminjaman.getBuku().getJudulBuku(),
                                peminjaman.getUser().getUsername(),
                                peminjaman.getTanggalKembali());

                        peminjaman.setStatusBukuPinjaman(StatusBukuPinjaman.DENDA);
                        peminjamanBukuRepository.save(peminjaman);
                        updatedCount++;

                    } catch (Exception e) {
                        log.error("Failed to update peminjaman ID {}: {}", peminjaman.getId(), e.getMessage());
                    }
                }

                log.info("Successfully updated {} peminjaman to DENDA status", updatedCount);
            }

            log.info("=== Scheduled check for overdue peminjaman completed ===");

        } catch (Exception e) {
            log.error("Error in scheduled check for overdue peminjaman: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual trigger method untuk testing atau menjalankan on-demand
     * Bisa dipanggil melalui endpoint jika diperlukan
     */
    @Transactional
    public void checkOverduePeminjamanManual() {
        log.info("=== MANUAL TRIGGER: Checking for overdue peminjaman ===");
        checkOverduePeminjaman();
    }
}
