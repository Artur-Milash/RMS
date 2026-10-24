package com.rms.restaurant_management_system.order.dto;

import com.rms.restaurant_management_system.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusUpdateRequest(

        @NotNull
        OrderStatus status
) {
}
