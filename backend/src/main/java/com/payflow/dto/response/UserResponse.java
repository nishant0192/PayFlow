package com.payflow.dto.response;

import com.payflow.enums.KycStatus;
import com.payflow.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String phone;
    private String firstName;
    private String lastName;
    private UserRole role;
    private KycStatus kycStatus;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
}