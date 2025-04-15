package com.exam.service;

import com.exam.model.PasswordResetToken;
import com.exam.model.User;

public interface PasswordResetTokenService {
    void createPasswordResetTokenForUser(User user, String token);
    PasswordResetToken getToken(String token);
    void sendPasswordResetEmail(String email, String token);
}
