package com.rms.restaurant_management_system.auth.dto;

import com.rms.restaurant_management_system.user.model.UserRole;

public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        Long userId,
        String login,
        String fullName,
        UserRole role
) {
}