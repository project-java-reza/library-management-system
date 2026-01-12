package com.sinaukoding.librarymanagementsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationConfig jwtAuthenticationConfig;
    private final AccessDeniedConfig accessDeniedConfig;
    private final AuthenticationEntryPointConfig authenticationEntryPointConfig;
    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/auth/register/admin/**").permitAll()
//                        .requestMatchers("/auth/register/user/**").permitAll()
//                        .requestMatchers("/auth/login/**").permitAll()
//                        .requestMatchers("/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**",
//                        "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/webjars/**",
//                        "/swagger-ui.html").permitAll()
                        // Auth endpoints - register and login only
                        .requestMatchers("/auth/register").permitAll()
                        .requestMatchers("/auth/register/**").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/register/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        // Logout requires authentication
                        .requestMatchers("/auth/logout").authenticated()
                        .requestMatchers("/api/auth/logout").authenticated()
                        // Swagger UI & API Documentation
                        .requestMatchers("/swagger").permitAll()
                        .requestMatchers("/swagger/").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/swagger-ui.html").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/v3/api-docs").permitAll()
                        .requestMatchers("/swagger-resources/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/configuration/**").permitAll()
                        // File uploads - public access for profile photos
                        .requestMatchers("/uploads/**").permitAll()
                        // Any other request needs authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPointConfig)
                        .accessDeniedHandler(accessDeniedConfig)
                )
                .addFilterBefore(jwtAuthenticationConfig, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
