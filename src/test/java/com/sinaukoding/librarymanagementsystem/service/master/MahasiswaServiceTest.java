package com.sinaukoding.librarymanagementsystem.service.master;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.entity.master.Mahasiswa;
import com.sinaukoding.librarymanagementsystem.mapper.master.MahasiswaMapper;
import com.sinaukoding.librarymanagementsystem.model.request.MahasiswaProfileRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.repository.master.MahasiswaRepository;
import com.sinaukoding.librarymanagementsystem.service.master.impl.MahasiswaServiceImpl;
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
class MahasiswaServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MahasiswaMapper mahasiswaMapper;

    @Mock
    private MahasiswaRepository mahasiswaRepository;

    @InjectMocks
    private MahasiswaServiceImpl mahasiswaService;

    @Test
    void testAddProfileMahasiswaUser_Success() {
        MahasiswaProfileRequestRecord request = new MahasiswaProfileRequestRecord(
                "Rizqi Reza Ardiansyah", "12191843", "Teknik Informatika", "Jalan Raya", "085156811979"
        );
        String token = "Bearer token123";
        String username = "reza";

        when(jwtUtil.extractUsername(token.substring(7))).thenReturn(username);

        User user = new User();
        when(userRepository.findByUsername(username)).thenReturn(java.util.Optional.of(user));

        Mahasiswa mahasiswa = new Mahasiswa();
        when(mahasiswaRepository.save(mahasiswa)).thenReturn(mahasiswa);
        when(userRepository.save(user)).thenReturn(user);

        // When
        Mahasiswa result = mahasiswaService.addProfileMahasiswaUser(request, token);

        // Then
        assertNotNull(result);
        verify(mahasiswaRepository).save(mahasiswa);
    }


    @Test
    void editProfileMahasiswaUser() {
    }

    @Test
    void findAllProfileMahasiswaUser() {

    }

    @Test
    void findByIdMahasiswa() {
    }

    @Test
    void deleteByIdMahasiswaUser() {
    }
}