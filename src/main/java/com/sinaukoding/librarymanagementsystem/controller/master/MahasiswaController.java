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
@RequestMapping("user/mahasiswa")
@RequiredArgsConstructor
public class MahasiswaController {

    private final MahasiswaService mahasiswaService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

    @PostMapping("save")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@RequestBody MahasiswaRequestRecord request) {
           mahasiswaService.addProfileMahasiswaUser(request);
           return BaseResponse.ok("Data berhasil disimpan", null);
    }

//    @PostMapping("edit")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> edit(@RequestBody UserRequestRecord request) {
//        userService.edit(request);
//        return BaseResponse.ok("Data berhasil diubah", null);
//    }
//
//    @PostMapping("find-all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
//                                   @RequestBody UserFilterRecord filterRequest) {
//        return BaseResponse.ok(null, userService.findAll(filterRequest, pageable));
//    }
//
//    @GetMapping("find-by-id/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> findById(@PathVariable String id) {
//        return BaseResponse.ok(null, userService.findById(id));
//    }
//
//    @DeleteMapping("delete/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> deleteById(@PathVariable String id) {
//        userService.deleteById(id);
//        return BaseResponse.ok("Delete berhasil", null);
//
//    }

}
