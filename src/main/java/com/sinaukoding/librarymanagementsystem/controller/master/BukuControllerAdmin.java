package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.BukuSearchRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.BukuService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/buku")
public class BukuControllerAdmin extends BaseController {

    private final BukuService bukuService;

    public BukuControllerAdmin(BukuService bukuService) {
        this.bukuService = bukuService;
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> create(@Valid @RequestBody CreateBukuRequestRecord request) {
        String username = getCurrentUsername();
        bukuService.addBukuBaru(request, username);
        return BaseResponse.ok("Data Buku berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@RequestBody UpdateBukuRequestRecord request) {
        String username = getCurrentUsername();
        bukuService.editBuku(request, username);
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
