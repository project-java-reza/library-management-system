package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.model.request.BukuSearchRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/user/buku")
@RequiredArgsConstructor
public class BukuControllerUser {

    private final BukuService bukuService;

    @PostMapping("/find-all")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> findAll(@RequestBody BukuSearchRequestRecord request) {
        return BaseResponse.ok(null, bukuService.findAllBuku(request));
    }

    @PostMapping("/find/{id}")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, bukuService.findByIdBuku(id));
    }
}
