package com.rms.restaurant_management_system.restaurant_table.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TableCreateRequest(

        @NotNull
        @Min(1)
        Integer number,

        @NotNull
        @Min(1)
        Integer seatsCount
) {
}
