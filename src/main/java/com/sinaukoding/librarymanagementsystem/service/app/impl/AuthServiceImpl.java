package com.sinaukoding.librarymanagementsystem.service.app.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.exception.AuthenticationException;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.UserMapper;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.LoginRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.RegisterRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
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
    private final MahasiswaRepository mahasiswaRepository;

    @Override
    public SimpleMap login(LoginRequestRecord request) {
        validatorService.validator(request);

        // Try to login as user first
        try {
            return loginUserWithData(request);
        } catch (AuthenticationException e) {
            // If user login fails, try admin
            return loginAdminWithData(request);
        }
    }

    @Override
    public SimpleMap loginUser(LoginRequestRecord request) {
        return loginUserWithData(request);
    }

    private SimpleMap loginUserWithData(LoginRequestRecord request) {
        validatorService.validator(request);
        var user = userRepository.findByUsername(request.username().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Username atau password salah"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new AuthenticationException("Username atau password salah");
        }

        String token = jwtUtil.generateToken(user.getUsername());
        user.setToken(token);
        user.setExpiredTokenAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        SimpleMap result = new SimpleMap();
        result.put("token", token);
        result.put("id", user.getId());
        result.put("username", user.getUsername());
        result.put("email", user.getEmail());
        result.put("nama", user.getNama());
        result.put("role", user.getRole().getRole().name());
        return result;
    }

    @Override
    public SimpleMap loginAdmin(LoginRequestRecord request) {
        return loginAdminWithData(request);
    }

    private SimpleMap loginAdminWithData(LoginRequestRecord request) {
        validatorService.validator(request);
        var admin = adminRepository.findByUsername(request.username().toLowerCase())
                .orElseThrow(() -> new AuthenticationException("Username atau password salah"));

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new AuthenticationException("Username atau password salah");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        admin.setToken(token);
        admin.setExpiredTokenAt(LocalDateTime.now().plusHours(1));
        adminRepository.save(admin);

        SimpleMap result = new SimpleMap();
        result.put("token", token);
        result.put("id", admin.getId());
        result.put("username", admin.getUsername());
        result.put("email", admin.getEmail());
        result.put("nama", admin.getNama());
        result.put("role", admin.getRole().getRole().name());
        return result;
    }

    @Override
    public void registerAdmin(AdminRegisterRequestRecord request) {
        try {
            validasiMandatory(request);

            if (adminRepository.existsByUsername(request.username().toLowerCase())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }

            if (adminRepository.existsByEmail(request.email().toLowerCase())) {
                throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
            }

            Role adminRole = roleService.getOrSave(ERole.ADMIN);

            Admin admin = Admin.builder()
                    .nama(request.nama())
                    .username(request.username().toLowerCase())
                    .email(request.email().toLowerCase())
                    .password(passwordEncoder.encode(request.password()))
                    .role(adminRole)
                    .status(Status.AKTIF)
                    .build();

            adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Admin already exists");
        }
    }

    @Override
    public void register(RegisterRequestRecord request) {
        try {
            // Validate
            if (request.username() == null || request.username().isEmpty()) {
                throw new RuntimeException("Username tidak boleh kosong");
            }
            if (request.email() == null || request.email().isEmpty()) {
                throw new RuntimeException("Email tidak boleh kosong");
            }
            if (request.password() == null || request.password().isEmpty()) {
                throw new RuntimeException("Password tidak boleh kosong");
            }

            // Check if username already exists
            if (userRepository.existsByUsername(request.username().toLowerCase())) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan");
            }

            // Check if email already exists
            if (userRepository.existsByEmail(request.email().toLowerCase())) {
                throw new RuntimeException("Email [" + request.email() + "] sudah digunakan");
            }

            // Check if NIM already exists
            if (mahasiswaRepository.existsByNim(request.nim())) {
                throw new RuntimeException("NIM [" + request.nim() + "] sudah digunakan");
            }

            // Create User
            Role userRole = roleService.getOrSave(ERole.ANGGOTA);
            User user = User.builder()
                    .nama(request.nama())
                    .username(request.username().toLowerCase())
                    .email(request.email().toLowerCase())
                    .password(passwordEncoder.encode(request.password()))
                    .role(userRole)
                    .status(Status.AKTIF)
                    .build();

            User savedUser = userRepository.save(user);

            // Create Mahasiswa
            Mahasiswa mahasiswa = Mahasiswa.builder()
                    .user(savedUser)
                    .nama(request.nama())
                    .nim(request.nim())
                    .jurusan(request.jurusan())
                    .alamat("")
                    .phoneNumber("")
                    .build();

            mahasiswaRepository.save(mahasiswa);

        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "User already exists");
        }
    }

    @Override
    public void registerUser(UserRequestRecord request) {
        try {
            validasiMandatory(request);

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

    @Override
    public void logoutUser(User userLoggedIn) {
        userLoggedIn.setToken(null);
        userLoggedIn.setExpiredTokenAt(null);
        userRepository.save(userLoggedIn);
    }

    private void validasiMandatory(AdminRegisterRequestRecord request) {
        if (request.nama() == null || request.nama().isEmpty()) {
            throw new RuntimeException("Nama tidak boleh kosong");
        }

        if (request.username() == null || request.username().isEmpty()) {
            throw new RuntimeException("Username tidak boleh kosong");
        }

        if (request.email() == null || request.email().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }

        if (request.password() == null || request.password().isEmpty()) {
            throw new RuntimeException("Password tidak boleh kosong");
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