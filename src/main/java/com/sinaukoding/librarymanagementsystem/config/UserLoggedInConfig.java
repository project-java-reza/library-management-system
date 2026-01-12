package com.sinaukoding.librarymanagementsystem.config;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@Slf4j
public class UserLoggedInConfig implements UserDetails {

    private final Admin admin;
    private final User user;

    public UserLoggedInConfig(Admin admin) {
        this.admin = admin;
        this.user = null;
    }

    public UserLoggedInConfig(User user) {
        this.user = user;
        this.admin = null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (admin != null) {
            String roleName = "ROLE_" + admin.getRole().getRole().name();
            log.info("Admin authorities: {}", roleName);
            log.info("Admin username: {}, Role enum value: {}", admin.getUsername(), admin.getRole().getRole());
            return List.of(new SimpleGrantedAuthority(roleName));  // Role Admin
        }
        else if (user != null) {
            String roleName = "ROLE_" + user.getRole().getRole().name();
            log.info("User authorities: {}", roleName);
            log.info("User username: {}, Role enum value: {}", user.getUsername(), user.getRole().getRole());
            return List.of(new SimpleGrantedAuthority(roleName));  // Role User
        }
        log.error("No authorities - both admin and user are null");
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        if (admin != null) {
            return admin.getPassword();
        } else {
            assert user != null;
            return user.getPassword();
        }
    }

    @Override
    public String getUsername() {
        if (admin != null) {
            return admin.getUsername();
        } else if (user != null){
            return user.getUsername();
        }
        return null;
    }

    public String getId() {
        if (admin != null) {
            log.info("Admin ID: " + admin.getId());
            return admin.getId();
        } else if (user != null) {
            log.info("User ID: " + user.getId());
            return user.getId();
        }
        log.error("Tidak ada Admin atau User yang ditemukan.");
        return null;
    }




}
