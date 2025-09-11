package com.sinaukoding.librarymanagementsystem.mapper.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.model.request.AdminRequestRecord;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import org.springframework.stereotype.Component;

@Component
public class AdminMapper {

    private final RoleService roleService;

    // Inject ROLE_ADMIN untuk mendapatkan role admin
    public AdminMapper(RoleService roleService) {
        this.roleService = roleService;
    }

    public Admin requestToEntity(AdminRequestRecord request) {
        return Admin.builder()
                .nama(request.nama().toUpperCase())
                .username(request.username().toLowerCase())
                .email(request.email().toLowerCase())
                .status(request.status())
                .build();
    }

}
