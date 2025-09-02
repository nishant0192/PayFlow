package com.payflow.service.impl;

import com.payflow.dto.request.LoginRequest;
import com.payflow.dto.request.RegisterRequest;
import com.payflow.dto.response.AuthResponse;
import com.payflow.dto.response.UserResponse;
import com.payflow.entity.User;
import com.payflow.enums.KycStatus;
import com.payflow.enums.UserRole;
import com.payflow.exception.AuthenticationException;
import com.payflow.exception.PayflowException;
import com.payflow.exception.UserNotFoundException;
import com.payflow.repository.UserRepository;
import com.payflow.security.JwtTokenProvider;
import com.payflow.service.AuthService;
import com.payflow.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ValidationUtils validationUtils;

    @Override
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        // Validate input
        validateRegistrationRequest(request);

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new PayflowException("Email already registered");
        }

        if (userRepository.existsByPhone(request.getPhone())) {
            throw new PayflowException("Phone number already registered");
        }

        // Create new user
        User user = User.builder()
                .email(validationUtils.sanitizeInput(request.getEmail().toLowerCase()))
                .phone(validationUtils.sanitizeInput(request.getPhone()))
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .firstName(validationUtils.sanitizeInput(request.getFirstName()))
                .lastName(validationUtils.sanitizeInput(request.getLastName()))
                .role(UserRole.USER)
                .kycStatus(KycStatus.PENDING)
                .isActive(true)
                .emailVerified(false)
                .phoneVerified(false)
                .loginAttempts(0)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Generate JWT token
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                request.getEmail(), request.getPassword());
        authentication = authenticationManager.authenticate(authentication);
        String token = tokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(tokenProvider.getExpirationTime())
                .user(convertToUserResponse(savedUser))
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getEmailOrPhone());

        // Find user
        User user = userRepository.findByEmailOrPhone(request.getEmailOrPhone())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        // Check if account is active
        if (!user.getIsActive()) {
            throw new AuthenticationException("Account is deactivated");
        }

        // Check login attempts
        if (user.getLoginAttempts() >= 5) {
            throw new AuthenticationException("Account locked due to multiple failed attempts");
        }

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmailOrPhone(),
                            request.getPassword()
                    )
            );

            // Reset login attempts on successful login
            user.setLoginAttempts(0);
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);

            // Generate JWT token
            String token = tokenProvider.generateToken(authentication);

            log.info("User logged in successfully: {}", user.getEmail());

            return AuthResponse.builder()
                    .token(token)
                    .tokenType("Bearer")
                    .expiresIn(tokenProvider.getExpirationTime())
                    .user(convertToUserResponse(user))
                    .build();

        } catch (Exception ex) {
            // Increment login attempts on failed login
            user.setLoginAttempts(user.getLoginAttempts() + 1);
            userRepository.save(user);
            throw new AuthenticationException("Invalid credentials");
        }
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return convertToUserResponse(user);
    }

    @Override
    public void logout(String token) {
        // In a real implementation, you might want to blacklist the token
        // For now, we'll just log the logout
        log.info("User logged out");
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    @Override
    public boolean isPhoneAvailable(String phone) {
        return !userRepository.existsByPhone(phone);
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (!validationUtils.isValidEmail(request.getEmail())) {
            throw new PayflowException("Invalid email format");
        }

        if (!validationUtils.isValidPhone(request.getPhone())) {
            throw new PayflowException("Invalid phone number format");
        }

        if (!validationUtils.isValidName(request.getFirstName())) {
            throw new PayflowException("Invalid first name");
        }

        if (!validationUtils.isValidName(request.getLastName())) {
            throw new PayflowException("Invalid last name");
        }

        if (!validationUtils.isStrongPassword(request.getPassword())) {
            throw new PayflowException("Password must contain at least 8 characters with uppercase, lowercase, digit and special character");
        }
    }

    private UserResponse convertToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .kycStatus(user.getKycStatus())
                .emailVerified(user.getEmailVerified())
                .phoneVerified(user.getPhoneVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .build();
    }
}