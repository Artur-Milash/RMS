package com.rms.restaurant_management_system.restaurant_table.dto;

import com.rms.restaurant_management_system.restaurant_table.model.TableStatus;

public record TableResponse(
        Long id,
        Integer number,
        Integer seatsCount,
        TableStatus status
) {
}