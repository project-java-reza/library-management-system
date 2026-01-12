package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.KategoriBukuMapper;
import com.sinaukoding.librarymanagementsystem.model.request.KategoriBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.impl.KategoriBukuImpl;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KategoriBukuServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private KategoriBukuMapper kategoriBukuMapper;

    @Mock
    private KategoriBukuRepository kategoriBukuRepository;

    @Mock
    private BukuRepository bukuRepository;

    @InjectMocks
    private KategoriBukuImpl kategoriBukuService;


    @Test
    void testAddKategoriBuku_Success() {
        KategoriBukuRequestRecord request = new KategoriBukuRequestRecord(
                "Kategori Buku 1", "Deskripsi Kategori Buku 1"
        );

        String token = "Bearer token123";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        Admin admin = new Admin();
        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));

        KategoriBuku kategoriBuku = new KategoriBuku();
        when(kategoriBukuMapper.requestToEntity(request)).thenReturn(kategoriBuku);

        when(kategoriBukuRepository.save(kategoriBuku)).thenReturn(kategoriBuku);

        // When
        KategoriBuku result = kategoriBukuService.addKategoriBuku(request, token);

        // Then
        assertNotNull(result);
        verify(kategoriBukuRepository).save(kategoriBuku);
    }

    @Test
    void testEditKategoriBuku_Success() {
        KategoriBukuRequestRecord request = new KategoriBukuRequestRecord(
                "kategori update", "deskripsi"
        );
        String token = "Bearer token123";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        Admin admin = new Admin();
        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));

        KategoriBuku existingKategoriBuku = new KategoriBuku();
        when(kategoriBukuRepository.findByAdmin(admin)).thenReturn(java.util.Optional.of(existingKategoriBuku));

        when(kategoriBukuRepository.save(existingKategoriBuku)).thenReturn(existingKategoriBuku);

        // When
        KategoriBuku result = kategoriBukuService.editKategoriBuku(request, token);

        // Then
        assertNotNull(result);
        verify(kategoriBukuRepository).save(existingKategoriBuku);
    }


    @Test
    void findAllKategoriBuku() {
    }

    @Test
    void findByIdKategoriBuku() {
    }

    @Test
    void testDeleteKategoriBuku_Success() {
        KategoriBuku kategoriBuku = new KategoriBuku();
        kategoriBuku.setId("1");
        when(kategoriBukuRepository.findById("1")).thenReturn(java.util.Optional.of(kategoriBuku));

        // When
        kategoriBukuService.deleteKategoriBuku("1");

        // Then
        verify(kategoriBukuRepository).deleteById("1");
    }
}