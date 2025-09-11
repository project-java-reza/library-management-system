package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.enums.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addUserTest() {
        UserRequestRecord requestUser1 = new UserRequestRecord(null,
                "Reza",
                "rezaa",
                "rizqirezaardiansyah@gmail.com",
                "reza123",
                Status.AKTIF,
                Role.PUSTAKAWAN
        );

        UserRequestRecord requestUser2 = new UserRequestRecord(null,
                "Rizqi",
                "rizqii",
                "rizqirezaardiansyahJava@gmail.com",
                "rizqi123",
                Status.AKTIF,
                Role.PUSTAKAWAN
        );
        User addUser1 = userService.add(requestUser1);
        User addUser2 = userService.add(requestUser2);

        UUID userId1 = UUID.fromString(addUser1.getId());
        UUID userId2 = UUID.fromString(addUser2.getId());

        System.out.println("User UUID 1" + userId1);
        System.out.println("User UUID 2" + userId2);
    }

    @Test
    void editUserTest() {
        UserRequestRecord existingUserRequest = new UserRequestRecord(
                "9776b045-3540-42d1-b54c-287874cfc544",
                "Rizqi",
                "rizqii",
                "rizqirezaardiansyah@gmail.com",
                "reza123",
                Status.AKTIF,
                Role.PUSTAKAWAN
        );

        userService.edit(existingUserRequest);
    }

    @Test
    void findUserAll() {
        List<User> result = userRepository.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("rezaa", result.get(0).getUsername());
        assertEquals("rizqii", result.get(1).getUsername());
    }

    @Test
    void findUserById() {
        UUID userId = UUID.fromString("1b7a2e7e-7205-4b68-8a08-1a11525b43c2");
        User result = userRepository.findById(String.valueOf(userId)).orElse(null);

        assertNotNull(result);
        assertEquals("rezaa", result.getUsername());
        assertEquals("REZA", result.getNama());
    }

    @Test
    void deleteUserById() {
        String userId = "9057e72d-cc80-4747-9a0b-b6a1c85f11a1";
        userService.deleteById(userId);
        assertTrue(true);
    }
}