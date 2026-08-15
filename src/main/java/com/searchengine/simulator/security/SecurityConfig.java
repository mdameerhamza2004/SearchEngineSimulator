package com.searchengine.simulator.security;

import com.searchengine.simulator.service.CustomOAuth2UserService;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Spring Security 6 configuration.
 * Configures stateless session management, CSRF disabling, URL protections, and OAuth2.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          CustomOAuth2UserService customOAuth2UserService,
                          JwtTokenProvider jwtTokenProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.customOAuth2UserService = customOAuth2UserService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt is standard for hashing passwords securely.
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for simplicity
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/forgot-password",
                    "/api/auth/reset-password",
                    "/api/topics/**",
                    "/api/search/**",
                    "/api/documents/**",
                    "/api/analytics/**",
                    "/api/history/**",
                    "/api/spell/**"
                ).permitAll()
                .requestMatchers("/api/auth/me", "/api/auth/logout").authenticated()
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService)
                )
                .successHandler((request, response, authentication) -> {
                    CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                    String token = jwtTokenProvider.generateToken(userDetails.getUsername());

                    Cookie cookie = new Cookie("auth_token", token);
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true); // Should be true in production (HTTPS)
                    cookie.setPath("/");
                    cookie.setAttribute("SameSite", "Strict");
                    cookie.setMaxAge(86400); // 1 day

                    response.addCookie(cookie);
                    response.sendRedirect("/");
                })
            );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
