package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/mahasiswa")
public class MahasiswaControllerUser extends BaseController {

    private final MahasiswaService mahasiswaService;

    public MahasiswaControllerUser(MahasiswaService mahasiswaService) {
        this.mahasiswaService = mahasiswaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@Valid @RequestBody MahasiswaProfileRequestRecord request) {
           String username = getCurrentUsername();
           mahasiswaService.addProfileMahasiswaUser(request, username);
           return BaseResponse.ok("Data Mahasiswa berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@Valid @RequestBody MahasiswaProfileRequestRecord request) {
        String username = getCurrentUsername();
        mahasiswaService.editProfileMahasiswaUser(request, username);
        return BaseResponse.ok("Data Mahasiswa berhasil diubah", null);
    }
}
