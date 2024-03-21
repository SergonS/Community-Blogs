package com.sergon.blogpost.service;

import com.sergon.blogpost.dto.RegisterRequest;
import com.sergon.blogpost.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthService
{
    @Autowired
    private PasswordEncoder passwordEncoder;

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
    }
}
