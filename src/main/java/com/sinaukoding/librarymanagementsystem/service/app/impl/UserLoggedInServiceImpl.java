package com.sinaukoding.librarymanagementsystem.service.app.impl;

import com.sinaukoding.librarymanagementsystem.config.UserLoggedInConfig;
import com.sinaukoding.librarymanagementsystem.model.app.Checks;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.UserRepository;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserLoggedInServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public UserDetails loadUserByUsernameForUser(String username) throws UsernameNotFoundException {
        var user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User dengan username: " + username + " tidak ditemukan"));

        Checks.isTrue(StringUtils.isNotBlank(user.getToken()), "Session habis, silahkan login kembali");

        return new UserLoggedInConfig(user);
    }

    public UserDetails loadAdminByUsername(String username) throws UsernameNotFoundException {
        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Admin dengan username: " + username + " tidak ditemukan"));

        Checks.isTrue(StringUtils.isNotBlank(admin.getToken()), "Session habis, silahkan login kembali");

        return new UserLoggedInConfig(admin);
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return loadUserByUsernameForUser(username);
        } catch (UsernameNotFoundException e) {
            return loadAdminByUsername(username);
        }
    }



}
