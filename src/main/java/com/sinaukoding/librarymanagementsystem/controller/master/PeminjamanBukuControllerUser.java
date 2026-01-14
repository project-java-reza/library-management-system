package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.IdRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchPeminjamanUserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/peminjaman")
@RequiredArgsConstructor
public class PeminjamanBukuControllerUser extends BaseController {

    private final PeminjamanBukuService peminjamanBukuService;

    @PostMapping
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@Valid @RequestBody PeminjamanBukuRequestRecord request) {
        String username = getCurrentUsername();
        peminjamanBukuService.addPeminjamanBuku(request, username);
        return BaseResponse.ok("Data Buku berhasil disimpan", null);
    }

    @PostMapping("/find-all")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getPeminjamanUser(@RequestBody SearchPeminjamanUserRequestRecord searchRequest) {
        String username = getCurrentUsername();
        return BaseResponse.ok("Berhasil mendapatkan data peminjaman", peminjamanBukuService.findPeminjamanByUser(username, searchRequest));
    }

    @PostMapping("/find-by-id")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getPeminjamanById(@RequestBody IdRequestRecord request) {
        return BaseResponse.ok(null, peminjamanBukuService.findByIdPeminjamanMahasiswa(request.id()));
    }

    @PostMapping("/{id}/return")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> returnBuku(@PathVariable String id) {
        peminjamanBukuService.returnBuku(id);
        return BaseResponse.ok("Buku berhasil dikembalikan", null);
    }
}
