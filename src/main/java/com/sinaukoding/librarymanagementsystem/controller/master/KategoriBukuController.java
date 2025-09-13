package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.enums.KategoriBukuEnum;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
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
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @GetMapping("/get-all-kategori-buku")
    @PreAuthorize("hasRole('ADMIN')")
    public List<String> getAllKategoriBuku() {
        // Mengambil semua kategori dari enum KategoriBukuEnum
        return Arrays.stream(KategoriBukuEnum.values())
                .map(KategoriBukuEnum::getLabel)
                .collect(Collectors.toList());
    }




}
