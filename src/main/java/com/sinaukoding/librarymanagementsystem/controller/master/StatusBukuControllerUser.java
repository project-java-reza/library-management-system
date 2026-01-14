package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class StatusBukuControllerUser {

    private final BukuService bukuService;

    @PostMapping("/status-buku/{id}")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getStatusBukuById(@PathVariable String id) {
        return BaseResponse.ok("Berhasil mendapatkan status buku", bukuService.getStatusBuku(id));
    }
}
