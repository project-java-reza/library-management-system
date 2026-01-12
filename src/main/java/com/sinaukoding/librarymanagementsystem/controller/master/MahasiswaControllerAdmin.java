package com.sinaukoding.librarymanagementsystem.controller.master;

import com.sinaukoding.librarymanagementsystem.model.filter.MahasiswaFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/admin/mahasiswa")
@RequiredArgsConstructor
public class MahasiswaControllerAdmin {

    private final MahasiswaService mahasiswaService;

    @PostMapping("/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@RequestBody SearchMahasiswaRequestRecord request) {
        // Convert SearchMahasiswaRequestRecord to MahasiswaFilterRecord
        MahasiswaFilterRecord filterRequest = new MahasiswaFilterRecord(
                request.search(),
                request.nama(),
                request.nim(),
                request.jurusan(),
                request.alamat()
        );

        // Create Pageable from request parameters with defaults for null values
        Pageable pageable = PageRequest.of(
                (request.pageNumber() != null ? request.pageNumber() : 1) - 1, // Spring Data uses 0-based page numbering
                request.pageSize() != null ? request.pageSize() : 10,
                Sort.by(
                        request.sortColumnDir() != null && request.sortColumnDir().equalsIgnoreCase("desc") ?
                                Sort.Direction.DESC :
                                Sort.Direction.ASC,
                        request.sortColumn() != null ? request.sortColumn() : "modifiedDate"
                )
        );

        return BaseResponse.ok(null, mahasiswaService.findAllProfileMahasiswaUser(filterRequest, pageable));
    }

    @PostMapping("/find/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, mahasiswaService.findByIdMahasiswa(id));
    }

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> create(@RequestBody CreateMahasiswaRequestRecord request) {
        return BaseResponse.ok("Data Mahasiswa berhasil disimpan", mahasiswaService.createMahasiswa(request));
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> update(@RequestBody UpdateMahasiswaRequestRecord request) {
        return BaseResponse.ok("Data Mahasiswa berhasil diubah", mahasiswaService.updateMahasiswa(request.id(), request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteById(@PathVariable String userId) {
        mahasiswaService.deleteByIdMahasiswaUser(userId);
        return BaseResponse.ok("Delete Data Mahasiswa dan User berhasil", null);
    }
}
