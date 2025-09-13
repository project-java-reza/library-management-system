package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @PostMapping("save")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@RequestBody AdminRequestRecord request) {
        adminService.create(request);
        return BaseResponse.ok("Data berhasil disimpan", null);
    }

    @PostMapping("edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@RequestBody AdminRequestRecord request) {
        adminService.edit(request);
        return BaseResponse.ok("Data berhasil diubah", null);
    }

    @PostMapping("find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllAdmin(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                   @RequestBody AdminFilterRecord filterRequest) {
        return BaseResponse.ok(null, adminService.findAllProfileAdmin(filterRequest, pageable));
    }

    @PostMapping("/user/find-all")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findAllUser(@PageableDefault(direction = Sort.Direction.DESC, sort = "modifiedDate") Pageable pageable,
                                   @RequestBody UserFilterRecord filterRequest) {
        return BaseResponse.ok(null, userService.findAllProfileUser(filterRequest, pageable));
    }

    @GetMapping("find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, adminService.findById(id));
    }

    @GetMapping("/user/find-by-id/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findByIdUser(@PathVariable String id) {
        return BaseResponse.ok(null, userService.findByIdUser(id));
    }

    @DeleteMapping("delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteByIdAdmin(@PathVariable String id) {
        adminService.deleteByIdAdmin(id);
        return BaseResponse.ok("Delete Admin berhasil", null);
    }

    @DeleteMapping("/user/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteByIdUser(@PathVariable String id) {
        userService.deleteByIdUser(id);
        return BaseResponse.ok("Delete User berhasil", null);
    }
}
