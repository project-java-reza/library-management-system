package com.sinaukoding.librarymanagementsystem.config;

import com.sinaukoding.librarymanagementsystem.entity.managementuser.Admin;
import com.sinaukoding.librarymanagementsystem.entity.managementuser.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
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
            return List.of(new SimpleGrantedAuthority("ROLE_" + admin.getRole().getRole().name()));  // Role Admin
        }
        else if (user != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().getRole().name()));  // Role User
        }
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        if (admin != null) {
            return admin.getPassword();
        } else {
            return user.getPassword();
        }
    }

    @Override
    public String getUsername() {
        if (admin != null) {
            return admin.getUsername();
        } else {
            return user.getUsername();
        }
    }

}
