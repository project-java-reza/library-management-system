package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.StatusBuku;
import com.sinaukoding.librarymanagementsystem.entity.master.Buku;
import com.sinaukoding.librarymanagementsystem.entity.master.KategoriBuku;
import com.sinaukoding.librarymanagementsystem.mapper.master.BukuMapper;
import com.sinaukoding.librarymanagementsystem.model.enums.EStatusBuku;
import com.sinaukoding.librarymanagementsystem.model.request.CreateBukuRequestRecord;
import com.sinaukoding.librarymanagementsystem.model.request.UpdateBukuRequestRecord;
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
        CreateBukuRequestRecord request = new CreateBukuRequestRecord(
                "buku 1", "penulis 1", "penerbit 1", 2023, "isbn-123", "kategoriId", 10, "deskripsi",
                "2", "A", "Fiksi", "R01", "B03"
        );

        String token = "Bearer token123";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        Admin admin = new Admin();
        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));

        KategoriBuku kategoriBuku = new KategoriBuku();
        kategoriBuku.setNamaKategoriBuku("kategori 1");
        when(kategoriBukuRepository.findById(request.kategoriId())).thenReturn(java.util.Optional.of(kategoriBuku));

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

    @Test
    void testEditBuku_Success() {
        UpdateBukuRequestRecord request = new UpdateBukuRequestRecord(
                "buku-id-123", "Bukuu", "Update Reza", "Jakarta Fair", 2023, "isbn-456",
                "kategoriId", 5, "deskripsi update", "3", "B", "Non-Fiksi", "R02", "B05", EStatusBuku.TERSEDIA
        );
        String token = "Bearer validToken";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        Admin admin = new Admin();
        when(adminRepository.findByUsername(username)).thenReturn(java.util.Optional.of(admin));

        Buku existingBuku = new Buku();
        existingBuku.setId("buku-id-123");
        when(bukuRepository.findById(request.id())).thenReturn(java.util.Optional.of(existingBuku));

        KategoriBuku kategoriBuku = new KategoriBuku();
        kategoriBuku.setNamaKategoriBuku("Kategori 1");
        when(kategoriBukuRepository.findById(request.kategoriId())).thenReturn(java.util.Optional.of(kategoriBuku));

        StatusBuku statusBuku = new StatusBuku();
        when(statusBukuRepository.findByStatusBuku(request.statusBuku())).thenReturn(java.util.Optional.of(statusBuku));

        when(bukuRepository.save(existingBuku)).thenReturn(existingBuku);

        // When
        Buku result = bukuService.editBuku(request, token);

        // Then
        assertNotNull(result);
        verify(bukuRepository).save(existingBuku);
    }

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
        buku.setIsbn("isbn-123");
        buku.setDeskripsi("deskripsi buku");
        // Set lokasi fields
        buku.setLantai("2");
        buku.setRuang("A");
        buku.setRak("Fiksi");
        buku.setNomorRak("R01");
        buku.setNomorBaris("B03");

        when(bukuRepository.findById("1")).thenReturn(java.util.Optional.of(buku));

        // When
        var result = bukuService.findByIdBuku("1");

        // Then
        assertNotNull(result);
        assertEquals("mencari sukses", result.get("judulBuku"));
        assertEquals("2", result.get("lantai"));
        assertEquals("A", result.get("ruang"));
        assertEquals("Fiksi", result.get("rak"));
        assertEquals("R01", result.get("nomorRak"));
        assertEquals("B03", result.get("nomorBaris"));
    }

    @Test
    void testDeleteByIdBuku_NotFound() {
        when(bukuRepository.findById("1")).thenReturn(java.util.Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> bukuService.deleteByIdBuku("1"));
        assertEquals("Data Buku tidak ditemukan", exception.getMessage());
    }
}