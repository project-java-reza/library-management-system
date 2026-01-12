package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.request.BukuSearchRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/buku")
public class BukuControllerAdmin {

    private final BukuService bukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> create(@Valid @RequestBody CreateBukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        bukuService.addBukuBaru(request, jwtToken);
        return BaseResponse.ok("Data Buku berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@RequestBody UpdateBukuRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        bukuService.editBuku(request, jwtToken);
        return BaseResponse.ok("Data Buku berhasil diubah", null);
    }

    @PostMapping("/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAll(@RequestBody BukuSearchRequestRecord request) {
        return BaseResponse.ok(null, bukuService.findAllBuku(request));
    }

    @PostMapping("/find/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, bukuService.findByIdBuku(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        bukuService.deleteByIdBuku(id);
        return BaseResponse.ok("Delete Data Buku Berhasil", null);
    }

}
