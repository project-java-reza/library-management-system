package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdatePeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.scheduler.PeminjamanScheduler;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/peminjaman")
@RequiredArgsConstructor
public class PeminjamanBukuControllerAdmin extends BaseController {

    private final PeminjamanBukuService peminjamanBukuService;
    private final PeminjamanScheduler peminjamanScheduler;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAll(@RequestBody SearchPeminjamanBukuRequestRecord searchRequest) {
        return BaseResponse.ok(null, peminjamanBukuService.findAllPeminjamanBuku(searchRequest));
    }

    @PostMapping("find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@RequestBody SearchPeminjamanBukuRequestRecord searchRequest) {
        return BaseResponse.ok(null, peminjamanBukuService.findAllPeminjamanBuku(searchRequest));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, peminjamanBukuService.findByIdPeminjamanMahasiswa(id));
    }

    @PostMapping("/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> update(@PathVariable String id, @RequestBody UpdatePeminjamanBukuRequestRecord request) {
        String username = getCurrentUsername();
        return BaseResponse.ok("Data Peminjaman Buku berhasil diubah", peminjamanBukuService.updatePeminjamanBuku(id, request, username));
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
        // Jalankan scheduler untuk mengecek keterlambatan
        peminjamanScheduler.checkOverduePeminjamanManual();

        // Update tanggal tenggat untuk peminjaman dengan status DENDA
        var result = peminjamanBukuService.cekPeminjamanTerlambat();

        return BaseResponse.ok("Pengecekan keterlambatan dan update tanggal tenggat selesai dijalankan.", result);
    }
}
