package com.sinaukoding.librarymanagementsystem.service.managementuser;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;

public interface RoleService {
    Role getOrSave(ERole role);
}
