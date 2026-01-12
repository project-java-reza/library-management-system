package com.sinaukoding.librarymanagementsystem.service.managementuser.impl;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Role;
import com.sinaukoding.librarymanagementsystem.model.enums.ERole;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.RoleRepository;
import com.sinaukoding.librarymanagementsystem.service.managementuser.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getOrSave(ERole role) {
        return roleRepository.findByRole(role).orElseGet(()-> roleRepository.save(Role.builder()
                .role(role)
                .build()));
    }
}
