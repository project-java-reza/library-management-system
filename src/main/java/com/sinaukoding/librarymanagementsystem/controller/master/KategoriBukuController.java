package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.enums.KategoriBukuEnum;
import com.sinaukoding.librarymanagementsystem.model.filter.KategoriBukuFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class KategoriBukuController {

    private final KategoriBukuService kategoriBukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/kategori-buku/save")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody KategoriBukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        kategoriBukuService.addKategoriBuku(request, jwtToken);
        return BaseResponse.ok("Data Kategori Buku berhasil disimpan", null);
    }

    @PostMapping("/kategori-buku/edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@RequestBody KategoriBukuRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        kategoriBukuService.editKategoriBuku(request, jwtToken);
        return BaseResponse.ok("Data Mahasiswa berhasil diubah", null);
    }

    @GetMapping("/get-all-kategori-buku")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getAllKategoriBuku() {
        return Arrays.stream(KategoriBukuEnum.values())
                .map(KategoriBukuEnum::getLabel)
                .collect(Collectors.toList());
    }

    @PostMapping("/kategori-buku/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate")Pageable pageable,
                                  @RequestBody KategoriBukuFilterRecord filterRequest) {
        return BaseResponse.ok(null, kategoriBukuService.findAllKategoriBuku(filterRequest, pageable));
    }

    @GetMapping("/kategori-buku/find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, kategoriBukuService.findByIdKategoriBuku(id));
    }

    @DeleteMapping("/kategori-buku/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        kategoriBukuService.deleteKategoriBuku(id);
        return BaseResponse.ok("Delete Data Kategori Buku Berhasil", null);
    }




}
