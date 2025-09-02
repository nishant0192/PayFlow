package com.payflow.service;

import com.payflow.dto.request.UpdateProfileRequest;
import com.payflow.dto.response.UserResponse;
import com.payflow.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(Long id);
    User getUserByEmail(String email);
    User getUserByPhone(String phone);
    User getUserByEmailOrPhone(String emailOrPhone);
    List<UserResponse> getAllUsers();
    UserResponse updateProfile(Long userId, UpdateProfileRequest request);
    void deactivateUser(Long userId);
    void activateUser(Long userId);
    Long getTotalActiveUsers();
    UserResponse convertToUserResponse(User user);
}