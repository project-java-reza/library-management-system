package com.sinaukoding.librarymanagementsystem.service.app.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.UserMapper;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.service.app.AuthService;
import com.sinaukoding.librarymanagementsystem.service.app.ValidatorService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final AdminMapper adminMapper;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ValidatorService validatorService;
    private final UserService userService;
    private final RoleService roleService;

    @Override
    public SimpleMap loginUser(LoginRequestRecord request) {
        validatorService.validator(request);
        var user = userRepository.findByUsername(request.username().toLowerCase()).orElseThrow(() -> new RuntimeException("Username atau password salah"));
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new RuntimeException("Username atau password salah");
        }
        String token = jwtUtil.generateToken(user.getUsername());
        user.setToken(token);
        user.setExpiredTokenAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);
        SimpleMap result = new SimpleMap();
        result.put("token", token);
        return result;
    }

    @Override
    public SimpleMap loginAdmin(LoginRequestRecord request) {
        validatorService.validator(request);
        var admin = adminRepository.findByUsername(request.username().toLowerCase()).orElseThrow(() -> new RuntimeException("Username atau password salah"));
        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new RuntimeException("Username atau password salah");
        }
        String token = jwtUtil.generateToken(admin.getUsername());
        admin.setToken(token);
        admin.setExpiredTokenAt(LocalDateTime.now().plusHours(1));
        adminRepository.save(admin);
        SimpleMap result = new SimpleMap();
        result.put("token", token);
        return result;
    }

    @Override
    public void registerAdmin(AdminRequestRecord request) {
        try {
            // validasi mandatory
            validasiMandatory(request);

            // validasi data existing
            if (adminRepository.existsByUsername(request.username().toLowerCase())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }

            Role adminRole = roleService.getOrSave(ERole.ADMIN);

            var admin = adminMapper.requestToEntity(request);
            admin.setUsername(request.username());
            admin.setPassword(passwordEncoder.encode(request.password()));
            admin.setRole(adminRole);
            adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exists");
        }
    }

    @Override
    public void registerUser(UserRequestRecord request) {
        try {
            // validasi mandatory
            validasiMandatory(request);

            // validasi data existing
            if (userRepository.existsByUsername(request.username().toLowerCase())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }

            Role userRole = roleService.getOrSave(ERole.ANGGOTA);

            var user = userMapper.requestToEntity(request);
            user.setUsername(request.username());
            user.setPassword(passwordEncoder.encode(request.password()));
            user.setRole(userRole);
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    @Override
    public void logout(Admin userLoggedIn) {
        userLoggedIn.setToken(null);
        userLoggedIn.setExpiredTokenAt(null);
        adminRepository.save(userLoggedIn);
    }

    private void validasiMandatory(AdminRequestRecord request) {
        if (request.username() == null || request.username().isEmpty()) {
            throw new RuntimeException("Username tidak boleh kosong");
        }

        if (request.password() == null || request.password().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }
    }

    private void validasiMandatory(UserRequestRecord request) {
        if (request.username() == null || request.username().isEmpty()) {
            throw new RuntimeException("Username tidak boleh kosong");
        }

        if (request.password() == null || request.password().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }
    }
}