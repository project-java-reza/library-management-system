package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class MahasiswaControllerUser {

    private final MahasiswaService mahasiswaService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("/mahasiswa/save")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@RequestBody MahasiswaRequestRecord request, HttpServletRequest httpServletRequest) throws Exception {
           String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
           mahasiswaService.addProfileMahasiswaUser(request, jwtToken);
           return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @PostMapping("/mahasiswa/edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@RequestBody MahasiswaRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        mahasiswaService.editProfileMahasiswaUser(request, jwtToken);
        return BaseResponse.ok("Data berhasil diubah", null);
    }
}
