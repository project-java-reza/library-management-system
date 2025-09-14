package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.BukuMapper;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.model.request.BukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.StatusBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.KategoriBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.StatusBukuService;
import com.sinaukoding.librarymanagementsystem.service.master.impl.BukuServiceImpl;
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
class BukuServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BukuRepository bukuRepository;

    @Mock
    private KategoriBukuRepository kategoriBukuRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private BukuMapper bukuMapper;

    @Mock
    private StatusBukuRepository statusBukuRepository;

    @Mock
    private StatusBukuService statusBukuService;

    @InjectMocks
    private BukuServiceImpl bukuService;

    @Test
    void testAddBukuBaru_Success() {
        BukuRequestRecord request = new BukuRequestRecord(
               "buku 1", "penulis 1", "penerbit 1", 2023, 10, "lokasi rak 1", "kategoriId", EStatusBuku.TERSEDIA
        );

        String token = "Bearer token123";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        Admin admin = new Admin();
        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));

        KategoriBuku kategoriBuku = new KategoriBuku();
        kategoriBuku.setNamaKategoriBuku("kategori 1");
        when(kategoriBukuRepository.findById(request.kategoriBukuId())).thenReturn(java.util.Optional.of(kategoriBuku));

        StatusBuku statusBuku = new StatusBuku();
        when(statusBukuService.getOrSave(EStatusBuku.TERSEDIA)).thenReturn(statusBuku);

        Buku bukuEntity = new Buku();
        when(bukuMapper.requestToEntity(request)).thenReturn(bukuEntity);
        when(bukuRepository.save(bukuEntity)).thenReturn(bukuEntity);

        // When
        Buku result = bukuService.addBukuBaru(request, token);

        // Then
        assertNotNull(result);
        verify(bukuRepository).save(bukuEntity);
    }

  //    @Test
//    void testEditBuku_Success() {
//        BukuRequestRecord request = new BukuRequestRecord(
//                "Bukuu", "Update Reza", "Jakarta Fair", 2023, 5, "Lokasi rak baru", "kategoriId", EStatusBuku.TERSEDIA
//        );
//        String token = "Bearer validToken";
//        String username = "reza";
//
//        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);
//
//        Admin admin = new Admin();
//        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));
//
//        KategoriBuku kategoriBuku = new KategoriBuku();
//        kategoriBuku.setNamaKategoriBuku("Kategori 1");
//        when(kategoriBukuRepository.findById(request.kategoriBukuId())).thenReturn(java.util.Optional.of(kategoriBuku));
//
//        Buku existingBuku = new Buku();
//        when(bukuRepository.findByAdmin(admin)).thenReturn(java.util.Optional.of(existingBuku));
//
//        Buku updatedBuku = new Buku();
//        when(bukuMapper.requestToEntity(request)).thenReturn(updatedBuku);
//        when(bukuRepository.save(updatedBuku)).thenReturn(updatedBuku);
//
//        // When
//        Buku result = bukuService.editBuku(request, token);
//
//        // Then
//        assertNotNull(result);
//        verify(bukuRepository).save(updatedBuku);
//    }

    @Test
    void findAllBuku() {
    }

    @Test
    void testFindByIdBuku_Success() {
        Buku buku = new Buku();
        buku.setId("1");
        buku.setJudulBuku("mencari sukses");
        buku.setPenulis("reza");
        buku.setPenerbit("Jakarta Fair");
        buku.setTahunTerbit(2023);
        buku.setJumlahSalinan(10);
        buku.setLokasiRak("rak E-20");

        when(bukuRepository.findById("1")).thenReturn(java.util.Optional.of(buku));

        // When
        var result = bukuService.findByIdBuku("1");

        // Then
        assertNotNull(result);
        assertEquals("mencari sukses", result.get("judulBuku"));
    }

    @Test
    void testDeleteByIdBuku_NotFound() {
        when(bukuRepository.findById("1")).thenReturn(java.util.Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bukuService.deleteByIdBuku("1"));
        assertEquals("Data Buku tidak ditemukan", exception.getMessage());
    }
}