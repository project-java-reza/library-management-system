package com.sinaukoding.librarymanagementsystem.config;

import com.sinaukoding.librarymanagementsystem.service.app.impl.UserLoggedInServiceImpl;
import com.sinaukoding.librarymanagementsystem.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationConfig extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserLoggedInServiceImpl userService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null) {
            if (authHeader.startsWith("Bearer ")) {
                jwt = authHeader.substring(7);
            } else {
                // Accept token without "Bearer " prefix
                jwt = authHeader;
            }
            try {
                username = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted Username: " + username);
            } catch (Exception e) {
                System.out.println("Failed to extract username: " + e.getMessage());
            }
        } else {
            System.out.println("No auth header found");
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userService.loadUserByUsername(username);

                if (jwtUtil.validateToken(jwt)) {
                    System.out.println("JWT Token validated successfully");
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Authentication set in SecurityContext");
                } else {
                    System.out.println("JWT Token validation failed");
                }
            } catch (Exception e) {
                System.out.println("Error loading user details: " + e.getMessage());
                e.printStackTrace();
            }
        }
        filterChain.doFilter(request, response);
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (headerAuth != null) {
            if (headerAuth.startsWith("Bearer ")) {
                return headerAuth.substring(7);
            } else {
                // Accept token without "Bearer " prefix
                return headerAuth;
            }
        }

        return null;
    }
}
