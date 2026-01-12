package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.StatusBukuPinjaman;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchRecentPeminjamanRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdatePeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.scheduler.PeminjamanScheduler;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/peminjaman")
@RequiredArgsConstructor
public class PeminjamanBukuControllerAdmin {

    private final PeminjamanBukuService peminjamanBukuService;
    private final PeminjamanScheduler peminjamanScheduler;

    /**
     * Map sort column from database column name (snake_case) to entity property name (camelCase)
     */
    private String mapSortColumn(String sortColumn) {
        if (sortColumn == null || sortColumn.isEmpty()) {
            return "modifiedDate";
        }

        return switch (sortColumn) {
            case "tanggal_pinjam" -> "tanggalPinjam";
            case "tanggal_kembali" -> "tanggalKembali";
            case "judul_buku", "judulBuku", "buku" -> "buku.judulBuku";
            case "nama", "nama_mahasiswa", "namaMahasiswa" -> "user.mahasiswa.nama";
            case "status_buku_pinjaman", "statusBukuPinjaman", "status" -> "statusBukuPinjaman";
            case "modified_date", "modifiedDate" -> "modifiedDate";
            case "created_date", "createdDate" -> "createdDate";
            default -> "modifiedDate";
        };
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAll(@RequestBody SearchPeminjamanBukuRequestRecord searchRequest) {
        // Map sort column from database name to entity property name
        String entitySortColumn = mapSortColumn(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "modifiedDate");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        PeminjamanBukuFilterRecord filterRequest = new PeminjamanBukuFilterRecord(
            null, null, null, null, null, null, null
        );
        if (searchRequest.status() != null && !searchRequest.status().isEmpty()) {
            try {
                StatusBukuPinjaman statusEnum =
                    StatusBukuPinjaman.valueOf(searchRequest.status().toUpperCase());
                filterRequest = new PeminjamanBukuFilterRecord(
                    null, null, null, null, statusEnum, null, null
                );
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        return BaseResponse.ok(null, peminjamanBukuService.findAllPeminjamanBuku(filterRequest, pageable));
    }

    @PostMapping("find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@RequestBody SearchPeminjamanBukuRequestRecord searchRequest) {
        // Map sort column from database name to entity property name
        String entitySortColumn = mapSortColumn(searchRequest.sortColumn());

        // Convert SearchRequestRecord to Pageable
        Sort sort = searchRequest.sortColumn() != null && !searchRequest.sortColumn().isEmpty()
                ? Sort.by(Sort.Direction.fromString(searchRequest.sortColumnDir() != null ? searchRequest.sortColumnDir() : "ASC"),
                          entitySortColumn)
                : Sort.by(Sort.Direction.DESC, "modifiedDate");

        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.pageNumber() - 1,
                searchRequest.pageSize(),
                sort
        );

        PeminjamanBukuFilterRecord filterRequest = new PeminjamanBukuFilterRecord(
            null, null, null, null, null, null, null
        );
        if (searchRequest.status() != null && !searchRequest.status().isEmpty()) {
            try {
                StatusBukuPinjaman statusEnum =
                    StatusBukuPinjaman.valueOf(searchRequest.status().toUpperCase());
                filterRequest = new PeminjamanBukuFilterRecord(
                    null, null, null, null, statusEnum, null, null
                );
            } catch (IllegalArgumentException e) {
                // Invalid status, ignore
            }
        }
        return BaseResponse.ok(null, peminjamanBukuService.findAllPeminjamanBuku(filterRequest, pageable));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, peminjamanBukuService.findByIdPeminjamanMahasiswa(id));
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> update(@PathVariable String id, @RequestBody UpdatePeminjamanBukuRequestRecord request) {
        return BaseResponse.ok("Data Peminjaman Buku berhasil diubah", peminjamanBukuService.updatePeminjamanBuku(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        peminjamanBukuService.deleteByIdPeminjamanMahasiswaSelesai(id);
        return BaseResponse.ok("Delete Data Peminjaman Buku berhasil", null);
    }

    @PostMapping("/{id}/persetujuan-pengembalian")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> approveReturn(@PathVariable String id) {
        peminjamanBukuService.approveReturnBuku(id);
        return BaseResponse.ok("Pengembalian buku berhasil disetujui. Stok buku telah dikembalikan.", null);
    }

    @PostMapping("/memeriksa-tenggat-pengembalian")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> checkOverduePeminjaman() {
        peminjamanScheduler.checkOverduePeminjamanManual();
        return BaseResponse.ok("Pengecekan keterlambatan peminjaman selesai dijalankan.", null);
    }
}
