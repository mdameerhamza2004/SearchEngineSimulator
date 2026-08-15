package com.searchengine.simulator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ConsoleEmailService implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(ConsoleEmailService.class);

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        logger.info("==============================================");
        logger.info("EMAIL TO: {}", to);
        logger.info("SUBJECT: Password Reset Request");
        logger.info("BODY:");
        logger.info("Please click the following link to reset your password:");
        logger.info(resetLink);
        logger.info("==============================================");
    }
}
