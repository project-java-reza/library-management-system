package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.model.request.StatusBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.StatusBukuService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/admin/status-buku")
@RequiredArgsConstructor
public class StatusBukuController {

    private final StatusBukuService statusBukuService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody StatusBukuRequestRecord request) {
        statusBukuService.getOrSave(request.statusBuku());
        return BaseResponse.ok("Data Status Buku berhasil disimpan", null);
    }

}
