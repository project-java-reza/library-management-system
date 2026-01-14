package com.sinaukoding.librarymanagementsystem.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Base Controller dengan helper methods untuk mendapatkan current user
 * Mengikuti best practices Spring Security dan Clean Code
 */
public abstract class BaseController {

    /**
     * Mendapatkan username dari SecurityContext
     * Token parsing sudah dilakukan oleh JwtAuthenticationFilter sebelum mencapai controller
     *
     * @return Username dari authenticated user
     */
    protected String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User tidak terautentikasi");
        }
        return authentication.getName();
    }

    /**
     * Mengecek apakah user sudah terautentikasi
     *
     * @return true jika user authenticated, false otherwise
     */
    protected boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());
    }

    /**
     * Mendapatkan authentication object dari SecurityContext
     *
     * @return Authentication object
     */
    protected Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
