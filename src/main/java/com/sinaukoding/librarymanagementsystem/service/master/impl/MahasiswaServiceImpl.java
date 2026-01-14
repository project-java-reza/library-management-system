package com.sinaukoding.librarymanagementsystem.service.master.impl;

import com.sinaukoding.librarymanagementsystem.builder.CustomBuilder;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.master.MahasiswaMapper;
import com.sinaukoding.librarymanagementsystem.model.app.AppPage;
import com.sinaukoding.librarymanagementsystem.model.app.SimpleMap;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.filter.MahasiswaFilterRecord;
import com.sinaukoding.librarymanagementsystem.model.request.CreateMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.SearchMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateMahasiswaRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import com.sinaukoding.librarymanagementsystem.service.managementuser.UserService;
import com.sinaukoding.librarymanagementsystem.service.master.MahasiswaService;
import com.sinaukoding.librarymanagementsystem.util.FilterUtil;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MahasiswaServiceImpl implements MahasiswaService {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserService userService;
    private final MahasiswaMapper mahasiswaMapper;
    private final MahasiswaRepository mahasiswaRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleService roleService;

    @Override
    public Mahasiswa addProfileMahasiswaUser(MahasiswaProfileRequestRecord request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        validasiMandatoryProfile(request);

        Mahasiswa mahasiswa = new Mahasiswa();

        mahasiswa.setNim(request.nim());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());

        mahasiswa.setNama(user.getNama());
        mahasiswa.setUser(user);
        user.setMahasiswa(mahasiswa);

        Mahasiswa simpanMahasiswa = mahasiswaRepository.save(mahasiswa);
        userRepository.save(user);

        return simpanMahasiswa;
    }

    @Override
    public Mahasiswa editProfileMahasiswaUser(MahasiswaProfileRequestRecord request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan."));

        validasiMandatoryProfile(request);

        if (mahasiswaRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new RuntimeException("Nomor HP [" + request.phoneNumber() + "] sudah digunakan");
        }

        Mahasiswa mahasiswa = mahasiswaRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Pengguna dengan " + username + " tidak ditemukan"));

        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());
        mahasiswa.setNim(request.nim());
        mahasiswa.setNama(user.getNama());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswaRepository.save(mahasiswa);
        return mahasiswa;
    }

    @Override
    public SimpleMap createMahasiswa(CreateMahasiswaRequestRecord request) {
        validasiMandatory(request);

        // Check if NIM already exists
        if (mahasiswaRepository.existsByNim(request.nim())) {
            throw new RuntimeException("NIM [" + request.nim() + "] sudah digunakan");
        }

        // Check if phone number already exists
        if (mahasiswaRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new RuntimeException("Nomor HP [" + request.phoneNumber() + "] sudah digunakan");
        }

        // Check if username already exists
        String username = request.username().toLowerCase().trim();
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username [" + request.username() + "] sudah digunakan. Silakan gunakan username lain.");
        }

        // Check if email already exists
        String email = request.email().toLowerCase().trim();
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email [" + request.email() + "] sudah digunakan. Silakan gunakan email lain.");
        }

        try {
            // Create User account
            Role userRole = roleService.getOrSave(ERole.ANGGOTA);
            User user = User.builder()
                    .nama(request.nama())
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(request.password()))
                    .role(userRole)
                    .status(Status.AKTIF)
                    .build();

            User savedUser = userRepository.save(user);

            // Create Mahasiswa linked to User
            Mahasiswa mahasiswa = Mahasiswa.builder()
                    .user(savedUser)
                    .nama(request.nama())
                    .nim(request.nim())
                    .jurusan(request.jurusan())
                    .alamat(request.alamat())
                    .phoneNumber(request.phoneNumber())
                    .build();

            Mahasiswa savedMahasiswa = mahasiswaRepository.save(mahasiswa);

            // Return SimpleMap instead of entity to avoid serialization issues
            SimpleMap data = new SimpleMap();
            data.put("id", savedMahasiswa.getId());
            data.put("nama", savedMahasiswa.getNama());
            data.put("nim", savedMahasiswa.getNim());
            data.put("jurusan", savedMahasiswa.getJurusan());
            data.put("alamat", savedMahasiswa.getAlamat());
            data.put("phoneNumber", savedMahasiswa.getPhoneNumber());
            data.put("username", savedUser.getUsername());
            data.put("email", savedUser.getEmail());
            data.put("status", savedMahasiswa.getUser() != null ? savedMahasiswa.getUser().getStatus().getLabel() : "INACTIVE");
            data.put("userId", savedUser.getId());
            data.put("role", savedUser.getRole() != null ? savedUser.getRole().getRole().getLabel() : null);
            return data;
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            log.error("Data integrity violation while creating mahasiswa: {}", e.getMessage());
            // Check if it's username constraint violation
            if (e.getMessage() != null && e.getMessage().contains("username")) {
                throw new RuntimeException("Username [" + request.username() + "] sudah digunakan. Silakan gunakan username lain.");
            }
            // Check if it's email constraint violation
            if (e.getMessage() != null && e.getMessage().contains("email")) {
                throw new RuntimeException("Email [" + request.email() + "] sudah digunakan. Silakan gunakan email lain.");
            }
            throw new RuntimeException("Terjadi kesalahan saat menyimpan data. Pastikan username, email, NIM, dan nomor HP belum pernah digunakan.");
        }
    }

    @Override
    public SimpleMap updateMahasiswa(String id, UpdateMahasiswaRequestRecord request) {
        // Validate mandatory fields for update
        validasiMandatoryUpdate(request);

        Mahasiswa mahasiswa = mahasiswaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Data Mahasiswa tidak ditemukan"));

        // Check if phone number is being changed and if it's already used
        if (!mahasiswa.getPhoneNumber().equals(request.phoneNumber()) &&
            mahasiswaRepository.existsByPhoneNumber(request.phoneNumber())) {
            throw new RuntimeException("Nomor HP [" + request.phoneNumber() + "] sudah digunakan");
        }

        // Check if NIM is being changed and if it's already used
        if (!mahasiswa.getNim().equals(request.nim()) &&
            mahasiswaRepository.existsByNim(request.nim())) {
            throw new RuntimeException("NIM [" + request.nim() + "] sudah digunakan");
        }

        // Update mahasiswa data
        mahasiswa.setNama(request.nama());
        mahasiswa.setNim(request.nim());
        mahasiswa.setJurusan(request.jurusan());
        mahasiswa.setAlamat(request.alamat());
        mahasiswa.setPhoneNumber(request.phoneNumber());

        // Update user data if exists
        User user = mahasiswa.getUser();
        if (user != null) {
            // Update email if provided
            if (request.email() != null && !request.email().trim().isEmpty()) {
                String newEmail = request.email().toLowerCase().trim();
                // Check if email is being changed and if it's already used
                if (!user.getEmail().equals(newEmail) &&
                    userRepository.existsByEmail(newEmail)) {
                    throw new RuntimeException("Email [" + request.email() + "] sudah digunakan. Silakan gunakan email lain.");
                }
                user.setEmail(newEmail);
            }

            // Update username if provided
            if (request.username() != null && !request.username().trim().isEmpty()) {
                String newUsername = request.username().toLowerCase().trim();
                // Check if username is being changed and if it's already used
                if (!user.getUsername().equals(newUsername) &&
                    userRepository.existsByUsername(newUsername)) {
                    throw new RuntimeException("Username [" + request.username() + "] sudah digunakan. Silakan gunakan username lain.");
                }
                user.setUsername(newUsername);
            }

            // Update password if provided
            if (request.password() != null && !request.password().trim().isEmpty()) {
                user.setPassword(passwordEncoder.encode(request.password()));
            }

            // Update status if provided
            if (request.status() != null && !request.status().trim().isEmpty()) {
                try {
                    Status newStatus = parseStatus(request.status());
                    if (newStatus != null) {
                        log.info("Updating status for user {} from {} to {}",
                            user.getUsername(), user.getStatus(), newStatus);
                        user.setStatus(newStatus);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid status value: {}", request.status());
                    throw new RuntimeException(e.getMessage());
                }
            }

            userRepository.save(user);
        } else {
            // If user tries to update email, username, password, or status but no user account exists
            if ((request.email() != null && !request.email().trim().isEmpty()) ||
                (request.username() != null && !request.username().trim().isEmpty()) ||
                (request.password() != null && !request.password().trim().isEmpty()) ||
                (request.status() != null && !request.status().trim().isEmpty())) {
                throw new RuntimeException("Mahasiswa ini tidak memiliki akun user. Tidak dapat mengupdate email, username, password, atau status.");
            }
        }

        Mahasiswa updatedMahasiswa = mahasiswaRepository.save(mahasiswa);

        // Return SimpleMap instead of entity
        SimpleMap data = new SimpleMap();
        data.put("id", updatedMahasiswa.getId());
        data.put("nama", updatedMahasiswa.getNama());
        data.put("nim", updatedMahasiswa.getNim());
        data.put("jurusan", updatedMahasiswa.getJurusan());
        data.put("alamat", updatedMahasiswa.getAlamat());
        data.put("phoneNumber", updatedMahasiswa.getPhoneNumber());
        data.put("status", updatedMahasiswa.getUser() != null ? updatedMahasiswa.getUser().getStatus().getLabel() : "INACTIVE");

        // Get username safely with fallback
        String username = null;
        try {
            if (updatedMahasiswa.getUser() != null && updatedMahasiswa.getUser().getUsername() != null) {
                username = updatedMahasiswa.getUser().getUsername();
            }
        } catch (Exception e) {
            log.warn("Error getting username from user for mahasiswa id: {}", updatedMahasiswa.getId(), e);
        }
        data.put("username", username);

        // Get email safely with fallback
        String email = null;
        try {
            if (updatedMahasiswa.getUser() != null && updatedMahasiswa.getUser().getEmail() != null) {
                email = updatedMahasiswa.getUser().getEmail();
            }
        } catch (Exception e) {
            log.warn("Error getting email from user for mahasiswa id: {}", updatedMahasiswa.getId(), e);
        }
        data.put("email", email);

        // Get userId safely with fallback
        String userId = null;
        try {
            if (updatedMahasiswa.getUser() != null && updatedMahasiswa.getUser().getId() != null) {
                userId = updatedMahasiswa.getUser().getId();
            }
        } catch (Exception e) {
            log.warn("Error getting userId from user for mahasiswa id: {}", updatedMahasiswa.getId(), e);
        }
        data.put("userId", userId);

        // Get role safely with fallback
        String role = null;
        try {
            if (updatedMahasiswa.getUser() != null && updatedMahasiswa.getUser().getRole() != null) {
                role = updatedMahasiswa.getUser().getRole().getRole().getLabel();
            }
        } catch (Exception e) {
            log.warn("Error getting role from user for mahasiswa id: {}", updatedMahasiswa.getId(), e);
        }
        data.put("role", role);

        return data;
    }

    @Override
    public Page<SimpleMap> findAllProfileMahasiswaUser(MahasiswaFilterRecord filterRequest, Pageable pageable) {
        CustomBuilder<Mahasiswa> builder = new CustomBuilder<>();

        FilterUtil.builderConditionNotBlankLike("nama", filterRequest.nama(), builder);
        FilterUtil.builderConditionNotBlankLike("nim", filterRequest.nim(), builder);
        FilterUtil.builderConditionNotBlankLike("jurusan", filterRequest.jurusan(), builder);
        FilterUtil.builderConditionNotBlankLike("alamat", filterRequest.alamat(), builder);
        FilterUtil.builderConditionNotBlankLike("phoneNumber", filterRequest.phoneNumber(), builder);

        Page<Mahasiswa> listMahasiswa = mahasiswaRepository.findAll(builder.build(), pageable);

        // Calculate Numbering Variables
        int recordsTotal = (int) listMahasiswa.getTotalElements();
        int recordsBeforeCurrentPage = (int) pageable.getOffset();

        // Determine sort direction from pageable
        boolean isAscending = true;
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            isAscending = order.isAscending();
        }
        boolean isDescending = !isAscending;

        List<SimpleMap> listData = new java.util.ArrayList<>();
        int index = 0;

        for (Mahasiswa mahasiswa : listMahasiswa.getContent()) {
            SimpleMap data = new SimpleMap();

            // Dynamic numbering berdasarkan sort direction
            int nomor;
            if (isDescending) {
                // DESC: Nomor mundur dari total
                nomor = recordsTotal - recordsBeforeCurrentPage - index;
            } else {
                // ASC: Nomor maju dari awal
                nomor = recordsBeforeCurrentPage + index + 1;
            }

            // Get nama safely with fallback
            String nama = mahasiswa.getNama(); // Default from mahasiswa entity
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getNama() != null) {
                    nama = mahasiswa.getUser().getNama();
                }
            } catch (Exception e) {
                // Fallback to mahasiswa.getNama() already set
                log.warn("Error getting nama from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            // Get status safely with fallback
            String status = "INACTIVE";
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getStatus() != null) {
                    status = mahasiswa.getUser().getStatus().getLabel();
                }
            } catch (Exception e) {
                // Keep default "INACTIVE"
                log.warn("Error getting status from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            // Get username safely with fallback
            String username = null;
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getUsername() != null) {
                    username = mahasiswa.getUser().getUsername();
                }
            } catch (Exception e) {
                log.warn("Error getting username from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            // Get email safely with fallback
            String email = null;
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getEmail() != null) {
                    email = mahasiswa.getUser().getEmail();
                }
            } catch (Exception e) {
                log.warn("Error getting email from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            // Get userId safely with fallback
            String userId = null;
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getId() != null) {
                    userId = mahasiswa.getUser().getId();
                }
            } catch (Exception e) {
                log.warn("Error getting userId from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            // Get role safely with fallback
            String role = null;
            try {
                if (mahasiswa.getUser() != null && mahasiswa.getUser().getRole() != null) {
                    role = mahasiswa.getUser().getRole().getRole().getLabel();
                }
            } catch (Exception e) {
                log.warn("Error getting role from user for mahasiswa id: {}", mahasiswa.getId(), e);
            }

            data.put("no", nomor);
            data.put("id", mahasiswa.getId());
            data.put("nama", nama);
            data.put("nim", mahasiswa.getNim());
            data.put("jurusan", mahasiswa.getJurusan());
            data.put("alamat", mahasiswa.getAlamat());
            data.put("phoneNumber", mahasiswa.getPhoneNumber());
            data.put("status", status);
            data.put("username", username);
            data.put("email", email);
            data.put("userId", userId);
            data.put("role", role);
            listData.add(data);
            index++;
        }

        return AppPage.create(listData, pageable, listMahasiswa.getTotalElements());
    }

    @Override
    public Page<SimpleMap> findAllProfileMahasiswaUser(SearchMahasiswaRequestRecord searchRequest) {
        // Convert SearchMahasiswaRequestRecord to MahasiswaFilterRecord
        MahasiswaFilterRecord filterRequest = new MahasiswaFilterRecord(
                searchRequest.search(),
                searchRequest.nama(),
                searchRequest.nim(),
                searchRequest.jurusan(),
                searchRequest.alamat()
        );

        // Create Pageable from request parameters with defaults for null values
        Pageable pageable = org.springframework.data.domain.PageRequest.of(
                (searchRequest.pageNumber() != null ? searchRequest.pageNumber() : 1) - 1, // Spring Data uses 0-based page numbering
                searchRequest.pageSize() != null ? searchRequest.pageSize() : 10,
                org.springframework.data.domain.Sort.by(
                        searchRequest.sortColumnDir() != null && searchRequest.sortColumnDir().equalsIgnoreCase("desc") ?
                                org.springframework.data.domain.Sort.Direction.DESC :
                                org.springframework.data.domain.Sort.Direction.ASC,
                        searchRequest.sortColumn() != null ? searchRequest.sortColumn() : "modifiedDate"
                )
        );

        // Call the original method with the built filter and pageable
        return findAllProfileMahasiswaUser(filterRequest, pageable);
    }

    @Override
    public SimpleMap findByIdMahasiswa(String id) {
        var mahasiswa = mahasiswaRepository.findById(id).orElseThrow(() -> new RuntimeException("Data Mahasiswa tidak ditemukan"));

        SimpleMap data = new SimpleMap();
        data.put("id", mahasiswa.getId());

        // Get nama safely with fallback
        String nama = mahasiswa.getNama(); // Default from mahasiswa entity
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getNama() != null) {
                nama = mahasiswa.getUser().getNama();
            }
        } catch (Exception e) {
            // Fallback to mahasiswa.getNama() already set
            log.warn("Error getting nama from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("nama", nama);

        // Get status safely with fallback
        String status = "INACTIVE";
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getStatus() != null) {
                status = mahasiswa.getUser().getStatus().getLabel();
            }
        } catch (Exception e) {
            // Keep default "INACTIVE"
            log.warn("Error getting status from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("status", status);

        // Get username safely with fallback
        String username = null;
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getUsername() != null) {
                username = mahasiswa.getUser().getUsername();
            }
        } catch (Exception e) {
            log.warn("Error getting username from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("username", username);

        // Get email safely with fallback
        String email = null;
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getEmail() != null) {
                email = mahasiswa.getUser().getEmail();
            }
        } catch (Exception e) {
            log.warn("Error getting email from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("email", email);

        // Get userId safely with fallback
        String userId = null;
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getId() != null) {
                userId = mahasiswa.getUser().getId();
            }
        } catch (Exception e) {
            log.warn("Error getting userId from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("userId", userId);

        // Get role safely with fallback
        String role = null;
        try {
            if (mahasiswa.getUser() != null && mahasiswa.getUser().getRole() != null) {
                role = mahasiswa.getUser().getRole().getRole().getLabel();
            }
        } catch (Exception e) {
            log.warn("Error getting role from user for mahasiswa id: {}", mahasiswa.getId(), e);
        }
        data.put("role", role);

        data.put("nim", mahasiswa.getNim());
        data.put("jurusan", mahasiswa.getJurusan());
        data.put("alamat", mahasiswa.getAlamat());
        data.put("phoneNumber", mahasiswa.getPhoneNumber());
        return data;
    }


    @Override
    public void deleteByIdMahasiswaUser(String userId) {
        // Find User first
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User tidak ditemukan"));

        // Find Mahasiswa by User
        Mahasiswa mahasiswa = mahasiswaRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Data Mahasiswa tidak ditemukan untuk user ini"));

        // Delete Mahasiswa first (due to foreign key constraint)
        mahasiswaRepository.delete(mahasiswa);

        // Then delete User
        userRepository.delete(user);
    }

    private void validasiMandatory(CreateMahasiswaRequestRecord request) {
        if (request.nama() == null || request.nama().isEmpty()) {
            throw new RuntimeException("Nama tidak boleh kosong");
        }
        if (request.nim() == null || request.nim().isEmpty()) {
            throw new RuntimeException("NIM tidak boleh kosong");
        }
        if (request.jurusan() == null || request.jurusan().isEmpty()) {
            throw new RuntimeException("Jurusan tidak boleh kosong");
        }
        if (request.alamat() == null || request.alamat().isEmpty()) {
            throw new RuntimeException("Alamat tidak boleh kosong");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isEmpty()) {
            throw new RuntimeException("Nomor HP tidak boleh kosong");
        }
        if (request.email() == null || request.email().isEmpty()) {
            throw new RuntimeException("Email tidak boleh kosong");
        }
        if (request.username() == null || request.username().isEmpty()) {
            throw new RuntimeException("Username tidak boleh kosong");
        }
        if (request.password() == null || request.password().isEmpty()) {
            throw new RuntimeException("Password tidak boleh kosong");
        }
    }

    private void validasiMandatoryUpdate(UpdateMahasiswaRequestRecord request) {
        if (request.nama() == null || request.nama().isEmpty()) {
            throw new RuntimeException("Nama tidak boleh kosong");
        }
        if (request.nim() == null || request.nim().isEmpty()) {
            throw new RuntimeException("NIM tidak boleh kosong");
        }
        if (request.jurusan() == null || request.jurusan().isEmpty()) {
            throw new RuntimeException("Jurusan tidak boleh kosong");
        }
        if (request.alamat() == null || request.alamat().isEmpty()) {
            throw new RuntimeException("Alamat tidak boleh kosong");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isEmpty()) {
            throw new RuntimeException("Nomor HP tidak boleh kosong");
        }
    }

    private void validasiMandatoryProfile(MahasiswaProfileRequestRecord request) {
        if (request.nama() == null || request.nama().isEmpty()) {
            throw new RuntimeException("Nama tidak boleh kosong");
        }
        if (request.nim() == null || request.nim().isEmpty()) {
            throw new RuntimeException("NIM tidak boleh kosong");
        }
        if (request.jurusan() == null || request.jurusan().isEmpty()) {
            throw new RuntimeException("Jurusan tidak boleh kosong");
        }
        if (request.alamat() == null || request.alamat().isEmpty()) {
            throw new RuntimeException("Alamat tidak boleh kosong");
        }
        if (request.phoneNumber() == null || request.phoneNumber().isEmpty()) {
            throw new RuntimeException("Nomor HP tidak boleh kosong");
        }
    }

    private Status parseStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }

        String statusUpper = status.trim().toUpperCase();

        try {
            return Status.valueOf(statusUpper);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                "Status tidak valid: '" + status + "'. Gunakan: AKTIF atau TIDAK_AKTIF"
            );
        }
    }
}
