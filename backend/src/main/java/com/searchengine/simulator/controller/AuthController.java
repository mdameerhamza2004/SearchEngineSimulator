package com.searchengine.simulator.controller;

import com.searchengine.simulator.model.User;
import com.searchengine.simulator.model.dto.auth.*;
import com.searchengine.simulator.security.CustomUserDetails;
import com.searchengine.simulator.security.JwtTokenProvider;
import com.searchengine.simulator.service.AuthService;
import com.searchengine.simulator.service.EmailService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final EmailService emailService;

    public AuthController(AuthService authService, JwtTokenProvider tokenProvider, EmailService emailService) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User user = authService.register(request);
            return ResponseEntity.ok(Map.of("message", "User registered successfully"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        try {
            User user = authService.authenticate(request);
            String token = tokenProvider.generateToken(user.getEmail());

            Cookie cookie = new Cookie("auth_token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(true); // Always secure in production
            cookie.setPath("/");
            cookie.setAttribute("SameSite", "Strict");
            cookie.setMaxAge(86400); // 1 day

            response.addCookie(cookie);

            return ResponseEntity.ok(new UserResponse(user));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("auth_token", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0); // clear cookie
        response.addCookie(cookie);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotRequest request) {
        String token = authService.createPasswordResetToken(request.getEmail());
        if (token != null) {
            String resetLink = "http://localhost:8080/reset-password?token=" + token;
            emailService.sendPasswordResetEmail(request.getEmail(), resetLink);
        }
        // Always return OK so as not to leak emails
        return ResponseEntity.ok(Map.of("message", "If an account exists, a reset link was sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetRequest request) {
        boolean success = authService.resetPassword(request.getToken(), request.getNewPassword());
        if (success) {
            return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid or expired token"));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(new UserResponse(userDetails.getUser()));
    }
}
