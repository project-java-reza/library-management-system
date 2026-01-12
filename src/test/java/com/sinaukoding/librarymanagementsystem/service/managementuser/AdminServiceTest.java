package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.AdminMapper;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.impl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private RoleService roleService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AdminMapper adminMapper;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void testCreateAdmin_Success() {
        AdminRequestRecord request = new AdminRequestRecord(
                null, "Rizqi Reza Ardiansyah", "reza", "rizqirezaardiansyah@gmail.com", "supersecret", ERole.ADMIN
        );

        Role adminRole = new Role();
        adminRole.setRole(ERole.ADMIN);
        when(roleService.getOrSave(ERole.ADMIN)).thenReturn(adminRole);

        Admin adminEntity = new Admin();
        when(adminMapper.requestToEntity(request)).thenReturn(adminEntity);
        when(passwordEncoder.encode(request.password())).thenReturn("supersecret");

        when(adminRepository.existsByEmail("rizqirezaardiansyah@gmail.com")).thenReturn(false);
        when(adminRepository.existsByUsername("reza")).thenReturn(false);

        // When
        Admin result = adminService.create(request);

        // Then
        assertNotNull(result);
        assertEquals("reza", result.getUsername());
        assertEquals("supersecret", result.getPassword());
        assertEquals("rizqirezaardiansyah@gmail.com", result.getEmail());
        verify(adminRepository).save(adminEntity);
    }

    @Test
    void testEditAdmin_Success() {
        AdminRequestRecord request = new AdminRequestRecord(
                "123", "Rizqi Reza Ardiansyah", "reza", "rizqirezaardiansyah@gmail.com", "passwordbaru", ERole.ADMIN
        );

        Admin existingAdmin = new Admin();
        existingAdmin.setId("123");
        existingAdmin.setUsername("reza");

        when(adminRepository.findById("123")).thenReturn(java.util.Optional.of(existingAdmin));
        when(adminRepository.existsByEmailAndIdNot("rizqirezaardiansyah@gmail.com", "123")).thenReturn(false);
        when(adminRepository.existsByUsernameAndIdNot("reza", "123")).thenReturn(false);

        Admin updatedAdmin = new Admin();
        when(adminMapper.requestToEntity(request)).thenReturn(updatedAdmin);
        when(passwordEncoder.encode(request.password())).thenReturn("passwordbaru");

        // When
        Admin result = adminService.edit(request);

        // Then
        assertNotNull(result);
        assertEquals("reza", result.getUsername());
        assertEquals("passwordbaru", result.getPassword());
        verify(adminRepository).save(updatedAdmin);
    }

    @Test
    void testFindById_Success() {
        Admin admin = new Admin();
        admin.setId("123");
        admin.setUsername("reza");
        admin.setEmail("rizqirezaardiansyah@gmail.com");
        admin.setNama("Rizqi Reza Ardiansyah");
        admin.setStatus(Status.AKTIF);  // Add Status to avoid NullPointerException

        when(adminRepository.findById("123")).thenReturn(java.util.Optional.of(admin));

        // When
        var result = adminService.findById("123");

        // Then
        assertNotNull(result);
        assertEquals("reza", result.get("username"));
        assertEquals("rizqirezaardiansyah@gmail.com", result.get("email"));
    }

    @Test
    void testDeleteByIdAdmin_Success() {
        Admin admin = new Admin();
        admin.setId("123");
        when(adminRepository.findById("123")).thenReturn(java.util.Optional.of(admin));

        // When
        adminService.deleteByIdAdmin("123");

        // Then
        verify(adminRepository).deleteById("123");
    }


}