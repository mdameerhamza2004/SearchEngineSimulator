package com.searchengine.simulator.service;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetLink);
}
