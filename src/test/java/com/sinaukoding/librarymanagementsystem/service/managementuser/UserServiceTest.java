package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.model.enums.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.Status;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    void addUserTest() {
        UserRequestRecord request = new UserRequestRecord(null,
                "Reza",
                "reza",
                "rizqirezaardiansyah@gmail.com",
                "fariz123",
                Status.AKTIF,
                Role.PUSTAKAWAN
        );
        userService.add(request);
    }

    @Test
    void edit() {
    }

    @Test
    void findAll() {
    }

    @Test
    void findById() {
    }
}