package com.sinaukoding.librarymanagementsystem.controller.managementuser;

import com.sinaukoding.librarymanagementsystem.config.JwtAuthenticationConfig;
import com.sinaukoding.librarymanagementsystem.model.request.UserProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtAuthenticationConfig jwtAuthenticationConfig;

//    @PostMapping("save")
//    @PreAuthorize("hasRole('ANGGOTA')")
//    public BaseResponse<?> save(@Valid @RequestBody UserRequestRecord request) {
//        userService.add(request);
//        return BaseResponse.ok("Data User berhasil disimpan", null);
//    }

    @PostMapping("edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> edit(@Valid @RequestBody UserRequestRecord request) {
        userService.edit(request);
        return BaseResponse.ok("Data User berhasil diubah", null);
    }

    @PostMapping("profile")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> getProfile(HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        return BaseResponse.ok("Berhasil mendapatkan profile user", userService.getProfileByToken(jwtToken));
    }

    @PostMapping("profile/edit")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> updateProfile(@Valid @RequestBody UserProfileRequestRecord request, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        userService.updateProfileByToken(request, jwtToken);
        return BaseResponse.ok("Berhasil mengupdate profile user", null);
    }

    @PostMapping("profile/upload")
    @PreAuthorize("hasRole('ANGGOTA')")
    public BaseResponse<?> uploadFoto(@RequestParam("file") MultipartFile file, HttpServletRequest httpServletRequest) {
        String jwtToken = jwtAuthenticationConfig.parseJwt(httpServletRequest);
        var userData = userService.getProfileByToken(jwtToken);
        String id = (String) userData.get("id");

        String fotoUrl = userService.uploadFoto(id, file);
        return BaseResponse.ok("Foto berhasil diupload", Map.of("fotoUrl", fotoUrl));
    }
}
