package com.sinaukoding.librarymanagementsystem.mapper.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User requestToEntity(UserRequestRecord request) {
        return User.builder()
                .nama(request.nama().toUpperCase())
                .username(request.username().toLowerCase())
                .email(request.email().toLowerCase())
                .status(request.status())
                .role(request.role())
                .build();
    }

}
