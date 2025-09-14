package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.mapper.master.PeminjamanBukuMapper;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.StatusBukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.BukuRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.PeminjamanBukuRepository;
import com.sinaukoding.librarymanagementsystem.service.master.impl.PeminjamanBukuServiceImpl;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

@ExtendWith(MockitoExtension.class)
class PeminjamanBukuServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PeminjamanBukuRepository peminjamanBukuRepository;

    @Mock
    private BukuRepository bukuRepository;

    @Mock
    private PeminjamanBukuMapper peminjamanBukuMapper;

    @Mock
    private StatusBukuRepository statusBukuRepository;

    @Mock
    private MahasiswaService mahasiswaService;

    @Mock
    private MahasiswaRepository mahasiswaRepository;

    @InjectMocks
    private PeminjamanBukuServiceImpl peminjamanBukuService;

//    @Test
//    void testAddPeminjamanBuku_Success() {
//        LocalDate tanggalPinjam = LocalDate.of(2023, 8, 1);
//        LocalDate tanggalKembali = LocalDate.of(2023, 8, 15);
//
//        PeminjamanBukuRequestRecord request = new PeminjamanBukuRequestRecord(
//                "bukuId", tanggalPinjam, tanggalKembali, StatusBukuPinjaman.DIPINJAM
//        );
//        String token = "Bearer token123";
//        String username = "user1";
//
//        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);
//
//        User user = new User();
//        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user));
//
//        Buku buku = new Buku();
//        when(bukuRepository.findById(request.bukuId())).thenReturn(java.util.Optional.of(buku));
//
//        when(statusBukuRepository.findByStatusBuku(EStatusBuku.TIDAK_TERSEDIA))
//                .thenReturn(java.util.Optional.of(new StatusBuku()));
//
//        PeminjamanBuku peminjamanBuku = new PeminjamanBuku();
//        when(peminjamanBukuMapper.requestToEntity(request)).thenReturn(peminjamanBuku);
//
//        when(peminjamanBukuRepository.save(peminjamanBuku)).thenReturn(peminjamanBuku);
//
//        // When
//        PeminjamanBuku result = peminjamanBukuService.addPeminjamanBuku(request, token);
//
//        // Then
//        assertNotNull(result);
//        verify(peminjamanBukuRepository).save(peminjamanBuku);
//    }

    @Test
    void findAllPeminjamanBuku() {

    }

    @Test
    void findByIdPeminjamanMahasiswa() {

    }

    @Test
    void deleteByIdPeminjamanMahasiswaSelesai() {

    }
}