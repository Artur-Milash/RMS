package com.rms.restaurant_management_system.order.order_item.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderItemCreateRequest(

        @NotNull
        Long menuItemId,

        @NotNull
        @Min(1)
        Integer quantity
) {
}
