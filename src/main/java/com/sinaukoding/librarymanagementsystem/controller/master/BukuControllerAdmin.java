package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("admin")
public class BukuControllerAdmin {

    private final BukuService bukuService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;


    @PostMapping("/buku/save")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody BukuRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        bukuService.addProfileMahasiswaUser(request, jwtToken);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }
}
