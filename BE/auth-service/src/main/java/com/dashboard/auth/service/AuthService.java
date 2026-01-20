package com.dashboard.auth.service;

import com.dashboard.auth.dto.*;
import com.dashboard.auth.entity.User;
import com.dashboard.auth.repository.UserRepository;
import com.dashboard.auth.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    
    @Value("${password.recovery.token.expiration}")
    private Long tokenExpiration;
    
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEnabled(true);
        
        user = userRepository.save(user);
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(
            token,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }
    
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        if (!user.getEnabled()) {
            throw new RuntimeException("Account is disabled");
        }
        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        String token = jwtUtil.generateToken(user.getEmail());
        
        return new AuthResponse(
            token,
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );
    }
    
    @Transactional
    public void requestPasswordRecovery(PasswordRecoveryRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Email not found"));
        
        String recoveryToken = UUID.randomUUID().toString();
        LocalDateTime expiry = LocalDateTime.now().plusSeconds(tokenExpiration / 1000);
        
        user.setPasswordRecoveryToken(recoveryToken);
        user.setPasswordRecoveryTokenExpiry(expiry);
        userRepository.save(user);
        
        // Send email with recovery token
        sendPasswordRecoveryEmail(user.getEmail(), recoveryToken);
    }
    
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByPasswordRecoveryToken(request.getToken())
            .orElseThrow(() -> new RuntimeException("Invalid or expired token"));
        
        if (user.getPasswordRecoveryTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token has expired");
        }
        
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setPasswordRecoveryToken(null);
        user.setPasswordRecoveryTokenExpiry(null);
        userRepository.save(user);
    }
    
    private void sendPasswordRecoveryEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Password Recovery");
            message.setText("To reset your password, use the following token:\n\n" + token + 
                          "\n\nThis token will expire in 24 hours.");
            mailSender.send(message);
        } catch (Exception e) {
            // Log error but don't fail the request
            log.error("Failed to send password recovery email to {}", email, e);
        }
    }
}
