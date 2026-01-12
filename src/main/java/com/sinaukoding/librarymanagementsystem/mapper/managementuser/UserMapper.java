package com.sinaukoding.librarymanagementsystem.mapper.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import com.sinaukoding.librarymanagementsystem.model.request.UserRequestRecord;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    private final RoleService roleService;

    public UserMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public User requestToEntity(UserRequestRecord request) {
        return User.builder()
                .nama(request.nama().toUpperCase())
                .username(request.username().toLowerCase())
                .email(request.email().toLowerCase())
                .status(request.status())
                .build();
    }

}
