package com.rms.restaurant_management_system.order.dto;

import com.rms.restaurant_management_system.order.order_item.dto.OrderItemCreateRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record OrderCreateRequest(

        @NotNull
        Long tableId,

        @NotEmpty
        @Valid
        List<OrderItemCreateRequest> items
) {
}
