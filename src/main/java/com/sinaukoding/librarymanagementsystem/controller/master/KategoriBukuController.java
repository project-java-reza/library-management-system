package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchKategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.KategoriBukuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/kategori")
public class KategoriBukuController extends BaseController {

    private final KategoriBukuService kategoriBukuService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@Valid @RequestBody KategoriBukuRequestRecord request) {
        String username = getCurrentUsername();
        kategoriBukuService.addKategoriBuku(request, username);
        return BaseResponse.ok("Data Kategori Buku berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@Valid @RequestBody KategoriBukuRequestRecord request) {
        String username = getCurrentUsername();
        kategoriBukuService.editKategoriBuku(request, username);
        return BaseResponse.ok("Data Kategori Buku berhasil diubah", null);
    }

    @PostMapping("find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAll(@RequestBody SearchKategoriBukuRequestRecord searchRequest) {
        return BaseResponse.ok(null, kategoriBukuService.findAllKategoriBuku(searchRequest));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, kategoriBukuService.findByIdKategoriBuku(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String id) {
        kategoriBukuService.deleteKategoriBuku(id);
        return BaseResponse.ok("Delete Data Kategori Buku Berhasil", null);
    }
}
