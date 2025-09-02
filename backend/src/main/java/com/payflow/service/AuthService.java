package com.payflow.service;

import com.payflow.dto.request.LoginRequest;
import com.payflow.dto.request.RegisterRequest;
import com.payflow.dto.response.AuthResponse;
import com.payflow.dto.response.UserResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    UserResponse getCurrentUser(Long userId);
    void logout(String token);
    boolean isEmailAvailable(String email);
    boolean isPhoneAvailable(String phone);
}