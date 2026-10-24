package com.rms.restaurant_management_system.user.dto;

import com.rms.restaurant_management_system.user.model.UserRole;

public record UserResponse(
        Long id,
        String fullName,
        String login,
        UserRole role,
        Boolean active
) {
}