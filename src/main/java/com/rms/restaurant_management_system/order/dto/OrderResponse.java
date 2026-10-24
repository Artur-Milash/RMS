package com.rms.restaurant_management_system.order.dto;

import com.rms.restaurant_management_system.order.model.OrderStatus;
import com.rms.restaurant_management_system.order.order_item.dto.OrderItemResponse;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        Long tableId,
        Integer tableNumber,
        Long waiterId,
        String waiterName,
        LocalDateTime createdAt,
        OrderStatus status,
        List<OrderItemResponse> items
) {
}
