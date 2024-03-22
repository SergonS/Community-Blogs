package com.sergon.blogpost.service;

import com.sergon.blogpost.dto.RegisterRequest;
import com.sergon.blogpost.model.User;
import com.sergon.blogpost.model.VerificationToken;
import com.sergon.blogpost.repository.UserRepository;
import com.sergon.blogpost.repository.VerificationTokenRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;

    @Transactional
    public void signup(RegisterRequest registerRequest)
    {
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        // Encrypting password
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        // Setting the creation date to now
        user.setCreated(Instant.now());
        // So as to have the user authenticate its email
        user.setEnabled(false);

        // Saving user
        userRepository.save(user);

        generateVerificationToken(user);
    }

    private String generateVerificationToken(User user)
    {
        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);

        // Saving token in repository
        verificationTokenRepository.save(verificationToken);

        return token;
    }
}
