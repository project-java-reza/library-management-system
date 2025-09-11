package com.sinaukoding.librarymanagementsystem.service.app.impl;

import com.sinaukoding.librarymanagementsystem.config.UserLoggedInConfig;
import com.sinaukoding.librarymanagementsystem.model.app.Checks;
import com.sinaukoding.librarymanagementsystem.repository.managementuser.AdminRepository;
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

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var admin = adminRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User : " + username + " tidak ditemukan"));
        Checks.isTrue(StringUtils.isNotBlank(admin.getToken()), "Session habis, silahkan login kembali");
        return new UserLoggedInConfig(admin);
    }

}
