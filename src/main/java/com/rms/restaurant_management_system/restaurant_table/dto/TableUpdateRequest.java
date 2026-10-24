package com.rms.restaurant_management_system.restaurant_table.dto;

import com.rms.restaurant_management_system.restaurant_table.model.TableStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record TableUpdateRequest(

        @NotNull
        @Min(1)
        Integer number,

        @NotNull
        @Min(1)
        Integer seatsCount,

        @NotNull
        TableStatus status
) {
}
