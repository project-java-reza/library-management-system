package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.controller.BaseController;
import com.sinaukoding.librarymanagementsystem.model.request.UserProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/user")
public class UserController extends BaseController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@Valid @RequestBody UserRequestRecord request) {
        userService.edit(request);
        return BaseResponse.ok("Data User berhasil diubah", null);
    }

    @PostMapping("profile")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getProfile() {
        String username = getCurrentUsername();
        return BaseResponse.ok("Berhasil mendapatkan profile user", userService.getProfileByUsername(username));
    }

    @PostMapping("profile/edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> updateProfile(@Valid @RequestBody UserProfileRequestRecord request) {
        String username = getCurrentUsername();
        userService.updateProfileByUsername(request, username);
        return BaseResponse.ok("Berhasil mengupdate profile user", null);
    }

    @PostMapping("profile/upload")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> uploadFoto(@RequestParam("file") MultipartFile file) {
        String username = getCurrentUsername();
        var userData = userService.getProfileByUsername(username);
        String id = (String) userData.get("id");

        String fotoUrl = userService.uploadFoto(id, file);
        return BaseResponse.ok("Foto berhasil diupload", Map.of("fotoUrl", fotoUrl));
    }
}
