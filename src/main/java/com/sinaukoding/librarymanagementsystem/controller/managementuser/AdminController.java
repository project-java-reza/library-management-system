package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.filter.AdminFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.filter.UserFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.ChangePasswordRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.AdminService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/admin")
public class AdminController extends BaseController {

    private final AdminService adminService;
    private final UserService userService;

    public AdminController(AdminService adminService, UserService userService) {
        this.adminService = adminService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> save(@Valid @RequestBody AdminRequestRecord request) {
        adminService.create(request);
        return BaseResponse.ok("Data Admin berhasil disimpan", null);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> edit(@Valid @RequestBody AdminRequestRecord request) {
        adminService.edit(request);
        return BaseResponse.ok("Data Admin berhasil diubah", null);
    }

    @PostMapping("/profile/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> editProfile(@Valid @RequestBody AdminProfileRequestRecord request) {
        adminService.editProfile(request);
        return BaseResponse.ok("Profile berhasil diperbarui", null);
    }

    @PostMapping("/profile")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> getProfile() {
        String username = getCurrentUsername();
        return BaseResponse.ok("Berhasil mendapatkan profile admin", adminService.findByUsername(username));
    }

    @PostMapping("/ubah-password")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequestRecord request) {
        adminService.changePassword(request);
        return BaseResponse.ok("Password berhasil diubah", null);
    }

    @PostMapping("/profile/upload")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> uploadFoto(@RequestParam("file") MultipartFile file) {
        String username = getCurrentUsername();
        var adminData = adminService.findByUsername(username);
        String id = (String) adminData.get("id");

        String fotoUrl = adminService.uploadFoto(id, file);
        return BaseResponse.ok("Foto berhasil diupload", Map.of("fotoUrl", fotoUrl));
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

    @PostMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findById(@PathVariable String id) {
        return BaseResponse.ok(null, adminService.findById(id));
    }

    @PostMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> findByIdUser(@PathVariable String id) {
        return BaseResponse.ok(null, userService.findByIdUser(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteByIdAdmin(@PathVariable String id) {
        adminService.deleteByIdAdmin(id);
        return BaseResponse.ok("Delete Data Admin berhasil", null);
    }

    @DeleteMapping("/user/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse<?> deleteByIdUser(@PathVariable String id) {
        userService.deleteByIdUser(id);
        return BaseResponse.ok("Delete Data User berhasil", null);
    }
}
