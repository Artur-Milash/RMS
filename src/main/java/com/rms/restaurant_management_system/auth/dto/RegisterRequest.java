package com.rms.restaurant_management_system.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank @Size(min = 3, max = 50) String login,
        @NotBlank @Size(min = 6) String password
) {
}
