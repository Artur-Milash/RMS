package com.rms.restaurant_management_system.order.order_item.dto;

import com.rms.restaurant_management_system.order.order_item.model.OrderItemStatus;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long id,
        Long menuItemId,
        String menuItemName,
        Integer quantity,
        OrderItemStatus status,
        BigDecimal priceAtOrder,
        BigDecimal totalPrice
) {
}
