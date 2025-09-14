package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.filter.PeminjamanBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.PeminjamanBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.PeminjamanBukuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class PeminjamanBukuControllerAdmin {

    private final PeminjamanBukuService peminjamanBukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/peminjaman-buku/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                        @RequestBody PeminjamanBukuFilterRecord filterRequest) {
        return BaseResponse.ok(null, peminjamanBukuService.findAllPeminjamanBuku(filterRequest, pageable));
    }

    @GetMapping("/peminjaman-buku/find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, peminjamanBukuService.findByIdPeminjamanMahasiswa(id));
    }

    @DeleteMapping("/peminjaman-buku/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        peminjamanBukuService.deleteByIdPeminjamanMahasiswaSelesai(id);
        return BaseResponse.ok("Delete Data Peminjaman Buku berhasil", null);
    }

}
