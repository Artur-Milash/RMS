package com.rms.restaurant_management_system.user.dto;

import com.rms.restaurant_management_system.user.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(

        @NotBlank
        String fullName,

        @NotNull
        UserRole role,

        @NotNull
        Boolean active
) {
}
