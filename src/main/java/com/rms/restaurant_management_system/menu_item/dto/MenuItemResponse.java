package com.rms.restaurant_management_system.menu_item.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Boolean active,
        Boolean available
) {
}