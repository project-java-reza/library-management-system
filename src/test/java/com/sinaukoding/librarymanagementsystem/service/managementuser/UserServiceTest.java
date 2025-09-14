package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.mapper.managementuser.UserMapper;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testAddUser_Success() {
        UserRequestRecord request = new UserRequestRecord(
                null, "Rizqi Reza Ardiansyah", "reza", "rizqirezaardiansyah@gmail.com", "supersecret", Status.AKTIF, ERole.ANGGOTA
        );

        when(userRepository.existsByEmail("rizqirezaardiansyah@gmail.com")).thenReturn(false);
        when(userRepository.existsByUsername("reza")).thenReturn(false);

        User userEntity = new User();
        when(userMapper.requestToEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode(request.password())).thenReturn("supersecret");

        // When
        User result = userService.add(request);

        // Then
        assertNotNull(result);
        assertEquals("reza", result.getUsername());
        assertEquals("supersecret", result.getPassword());
        assertEquals("rizqirezaardiansyah@gmail.com", result.getEmail());
        verify(userRepository).save(userEntity);
    }

    @Test
    void testEditUser_Success() {
        UserRequestRecord request = new UserRequestRecord(
                "123", "Rizqi Reza Ardiansyah Updated", "reza", "rizqirezaardiansyah@gmail.com", "newpassword", Status.AKTIF, ERole.ANGGOTA
        );

        User existingUser = new User();
        existingUser.setId("123");
        existingUser.setUsername("user1");

        when(userRepository.findById("123")).thenReturn(java.util.Optional.of(existingUser));
        when(userRepository.existsByEmailAndIdNot("rizqirezaardiansyah@gmail.com", "123")).thenReturn(false);
        when(userRepository.existsByUsernameAndIdNot("reza", "123")).thenReturn(false);

        User updatedUser = new User();
        when(userMapper.requestToEntity(request)).thenReturn(updatedUser);
        when(passwordEncoder.encode(request.password())).thenReturn("newpassword");

        // When
        User result = userService.edit(request);

        // Then
        assertNotNull(result);
        assertEquals("reza", result.getUsername());
        assertEquals("newpassword", result.getPassword());
        verify(userRepository).save(updatedUser);
    }

    @Test
    void findAllProfileUser() {
    }

//    @Test
//    void testFindByIdUser_Success() {
//        User user = new User();
//        user.setId("123");
//        user.setUsername("user1");
//        user.setEmail("user1@example.com");
//        user.setNama("User Name");
//        user.setStatus(Status.AKTIF);
//
//        when(userRepository.findById("123")).thenReturn(java.util.Optional.of(user));
//
//        // When
//        var result = userService.findByIdUser("123");
//
//        // Then
//        assertNotNull(result);
//        assertEquals("user1", result.get("username"));
//        assertEquals("user1@example.com", result.get("email"));
//    }


    @Test
    void deleteByIdUser() {
        User user = new User();
        user.setId("123");
        when(userRepository.findById("123")).thenReturn(java.util.Optional.of(user));

        // When
        userService.deleteByIdUser("123");

        // Then
        verify(userRepository).deleteById("123");
    }
}