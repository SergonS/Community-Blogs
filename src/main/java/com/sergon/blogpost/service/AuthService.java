package com.sergon.blogpost.service;

import com.sergon.blogpost.dto.LoginRequest;
import com.sergon.blogpost.dto.RegisterRequest;
import com.sergon.blogpost.exceptions.BlogpostException;
import com.sergon.blogpost.model.NotificationEmail;
import com.sergon.blogpost.model.User;
import com.sergon.blogpost.model.VerificationToken;
import com.sergon.blogpost.repository.UserRepository;
import com.sergon.blogpost.repository.VerificationTokenRepository;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AuthService
{
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;

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

        String token = generateVerificationToken(user);

        mailService.sendMail(new NotificationEmail("Please Activate your Account",
                user.getEmail(), "Thank you for signing up to Community Blogs, " + user.getUsername() +
                " please click on the url below to activate your account: " +
                "http://localhost:8080/api/auth/accountVerification/" + token));
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

    public void verifyAccount(String token)
    {
        // Search for token and retrieve it if found
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);

        // Throw exception due to token not being found
        verificationToken.orElseThrow(() -> new BlogpostException("Invalid Token"));

        fetchUserAndEnable(verificationToken.get());
    }

    @Transactional
    private void fetchUserAndEnable(VerificationToken verificationToken)
    {
        String username = verificationToken.getUser().getUsername();

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new BlogpostException("User not found with name - " + username));

        user.setEnabled(true);

        userRepository.save(user);
    }


    public void login(LoginRequest loginRequest)
    {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                loginRequest.getPassword()));


    }


}
