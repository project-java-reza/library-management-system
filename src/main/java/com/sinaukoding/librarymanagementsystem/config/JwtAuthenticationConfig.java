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
        System.out.println("\n======== JWT Filter START ========");
        System.out.println("Request URI: " + request.getRequestURI());
        System.out.println("Request Method: " + request.getMethod());

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Debug: Log request URI and auth header presence
        System.out.println("Auth Header Present: " + (authHeader != null));
        System.out.println("Auth Header Value: " + (authHeader != null && authHeader.length() > 20 ? authHeader.substring(0, 20) + "..." : authHeader));

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
                System.out.println("UserDetails loaded successfully for: " + username);
                System.out.println("User authorities: " + userDetails.getAuthorities());

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

        System.out.println("================================\n");
        filterChain.doFilter(request, response);
        System.out.println("======== JWT Filter END ========\n");
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
