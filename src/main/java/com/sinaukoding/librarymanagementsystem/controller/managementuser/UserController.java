package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("save")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> save(@RequestBody UserRequestRecord request) {
        userService.add(request);
        return BaseResponse.ok("Data User berhasil disimpan", null);
    }

    @PostMapping("edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@RequestBody UserRequestRecord request) {
        userService.edit(request);
        return BaseResponse.ok("Data User berhasil diubah", null);
    }
}
