package com.sinaukoding.librarymanagementsystem.controller.app;

import com.sinaukoding.librarymanagementsystem.config.UserLoggedInConfig;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.RegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.app.AuthService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public BaseResponse<?> login(@Valid @RequestBody LoginRequestRecord request) {
        return BaseResponse.ok("Berhasil Login", authService.login(request));
    }

    @PostMapping("pendaftaran")
    public BaseResponse<?> register(@Valid @RequestBody RegisterRequestRecord request) {
        authService.register(request);
        return BaseResponse.ok("Berhasil Mendaftar", null);
    }

    @PostMapping("pendaftaran/admin")
    public BaseResponse<?> registerAdmin(@Valid @RequestBody AdminRegisterRequestRecord request) {
        authService.registerAdmin(request);
        return BaseResponse.ok("Berhasil Mendaftar sebagai Admin", null);
    }

    @GetMapping("logout")
    @SecurityRequirement(name = "bearerAuth")
    public BaseResponse<?> logout(@AuthenticationPrincipal UserLoggedInConfig userLoggedInConfig) {
        if (userLoggedInConfig == null) {
            return BaseResponse.error("Tidak ada user yang login", null);
        }

        if (userLoggedInConfig.getAdmin() != null) {
            authService.logout(userLoggedInConfig.getAdmin());
        }

        if (userLoggedInConfig.getUser() != null) {
            authService.logout(userLoggedInConfig.getUser());
        }

        return BaseResponse.ok("Berhasil logout", null);
    }

}
