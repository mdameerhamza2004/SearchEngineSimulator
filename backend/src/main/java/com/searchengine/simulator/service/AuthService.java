package com.searchengine.simulator.service;

import com.searchengine.simulator.model.PasswordResetToken;
import com.searchengine.simulator.model.Provider;
import com.searchengine.simulator.model.User;
import com.searchengine.simulator.model.dto.auth.LoginRequest;
import com.searchengine.simulator.model.dto.auth.RegisterRequest;
import com.searchengine.simulator.repository.PasswordResetTokenRepository;
import com.searchengine.simulator.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
                       PasswordResetTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        // BCrypt hash
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setProvider(Provider.LOCAL);

        return userRepository.save(user);
    }

    public User authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (user.getProvider() != Provider.LOCAL) {
            throw new IllegalArgumentException("Please login with " + user.getProvider());
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return user;
    }

    @Transactional
    public String createPasswordResetToken(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return null; // Don't reveal if email exists
        }

        User user = userOpt.get();
        if (user.getProvider() != Provider.LOCAL) {
            return null; // Cannot reset oauth passwords here
        }

        String rawToken = UUID.randomUUID().toString();
        String hashedToken = hashToken(rawToken);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(hashedToken);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));

        tokenRepository.save(resetToken);

        return rawToken;
    }

    @Transactional
    public boolean resetPassword(String rawToken, String newPassword) {
        String hashedToken = hashToken(rawToken);
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByTokenHash(hashedToken);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken resetToken = tokenOpt.get();
        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            tokenRepository.delete(resetToken);
            return false;
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(resetToken);

        return true;
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
