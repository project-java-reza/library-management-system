package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("save")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody AdminRequestRecord request) {
        adminService.create(request);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

//    @PostMapping("edit")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> edit(@RequestBody AdminRequestRecord request) {
//        adminService.edit(request);
//        return BaseResponse.ok("Data berhasil diubah", null);
//    }

//    @PostMapping("find-all")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> findAll(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
//                                   @RequestBody UserFilterRecord filterRequest) {
//        return BaseResponse.ok(null, adminService.findAll(filterRequest, pageable));
//    }

    @GetMapping("find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, adminService.findById(id));
    }

//    @DeleteMapping("delete/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public BaseResponse<?> deleteById(@PathVariable String id) {
//        adminService.deleteById(id);
//        return BaseResponse.ok("Delete berhasil", null);
//
//    }

}
