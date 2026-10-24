package com.rms.restaurant_management_system.product.dto;

import com.rms.restaurant_management_system.product.model.ProductUnit;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        ProductUnit unit,
        BigDecimal stockQuantity
) {
}
