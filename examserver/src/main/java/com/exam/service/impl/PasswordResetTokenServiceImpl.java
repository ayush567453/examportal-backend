package com.exam.service.impl;

import com.exam.model.PasswordResetToken;
import com.exam.model.User;
import com.exam.repo.PasswordResetTokenRepository;
import com.exam.repo.UserRepository;
import com.exam.service.PasswordResetTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public PasswordResetToken getToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage passwordResetEmail = new SimpleMailMessage();
        passwordResetEmail.setFrom("no-reply@example.com");
        passwordResetEmail.setTo(email);
        passwordResetEmail.setSubject("Password Reset Request");
        passwordResetEmail.setText("To reset your password, click the link below:\n" + "http://localhost:4200/reset-password?token=" + token);
        mailSender.send(passwordResetEmail);
    }
}
