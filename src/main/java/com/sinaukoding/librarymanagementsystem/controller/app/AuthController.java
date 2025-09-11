package com.sinaukoding.librarymanagementsystem.controller.app;

import com.sinaukoding.librarymanagementsystem.config.UserLoggedInConfig;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.response.BaseResponse;
import com.sinaukoding.librarymanagementsystem.service.app.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("login")
    public BaseResponse<?> login(@RequestBody LoginRequestRecord request) {
        return BaseResponse.ok("Successfully Login", authService.login(request));
    }

    @PostMapping("register/admin")
    public BaseResponse<?> registerAdmin(@RequestBody AdminRequestRecord request) {
        authService.registerAdmin(request);
        return BaseResponse.ok("Successfully registered admin", null);
    }

    @PostMapping("register/user")
    public BaseResponse<?> registerUser(@RequestBody UserRequestRecord request) {
        authService.registerUser(request);
        return BaseResponse.ok("Successfully registered user", null);
    }

    @GetMapping("logout")
    public BaseResponse<?> logout(@AuthenticationPrincipal UserLoggedInConfig userLoggedInConfig) {
        var userLoggedIn = userLoggedInConfig.getUser();
        authService.logout(userLoggedIn);
        return BaseResponse.ok("Berhasil logout", null);
    }

}
