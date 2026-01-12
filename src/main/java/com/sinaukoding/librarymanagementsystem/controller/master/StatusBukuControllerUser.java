package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class StatusBukuControllerUser {

    private final BukuRepository bukuRepository;

    @GetMapping("status-buku/{id}")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getStatusBuku(@PathVariable String id) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Buku tidak ditemukan"));

        Map<String, Object> response = new HashMap<>();
        response.put("bukuId", buku.getId());
        response.put("judulBuku", buku.getJudulBuku());
        response.put("jumlahSalinan", buku.getJumlahSalinan());
        response.put("statusTersedia", buku.getStatusBukuTersedia().getStatusBuku().name());

        return BaseResponse.ok("Berhasil mendapatkan status buku", response);
    }
}
