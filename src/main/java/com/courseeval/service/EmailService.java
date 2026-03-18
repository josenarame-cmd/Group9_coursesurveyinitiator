package com.courseeval.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public boolean sendConfirmation(String to, String subject, String body) {
        if (mailSender == null || to == null || !to.contains("@") || !to.contains(".")) {
            log.warn("Invalid email or mail sender not configured.");
            return false;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
            log.info("Confirmation email successfully sent to {}", to);
            return true; // EMAIL SENT SUCCESSFULLY!

        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            System.err.println("=====================================================");
            System.err.println("EMAIL FAILED TO SEND! Gmail rejected the connection.");
            System.err.println("Error details: " + e.getMessage());
            System.err.println("=====================================================");
            return false; // EMAIL FAILED TO SEND
        }
    }
}