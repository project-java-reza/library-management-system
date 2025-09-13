package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.model.filter.MahasiswaFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class MahasiswaControllerAdmin {

    private final MahasiswaService mahasiswaService;

    @PostMapping("/mahasiswa/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                        @RequestBody MahasiswaFilterRecord filterRequest) {
        return BaseResponse.ok(null, mahasiswaService.findAllProfileMahasiswaUser(filterRequest, pageable));
    }

    @GetMapping("/mahasiswa/find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, mahasiswaService.findByIdMahasiswa(id));
    }

    @DeleteMapping("/mahasiswa/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        mahasiswaService.deleteByIdMahasiswaUser(id);
        return BaseResponse.ok("Delete Data User berhasil", null);
    }
}
