package com.rms.restaurant_management_system.user.dto;

import com.rms.restaurant_management_system.user.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UserCreateRequest(

        @NotBlank
        String fullName,

        @NotBlank
        @Size(min = 3, max = 50)
        String login,

        @NotBlank
        @Size(min = 6)
        String password,

        @NotNull
        UserRole role
) {
}
