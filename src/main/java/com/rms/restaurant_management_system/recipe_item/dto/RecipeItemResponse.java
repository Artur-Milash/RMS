package com.rms.restaurant_management_system.recipe_item.dto;

import com.rms.restaurant_management_system.product.model.ProductUnit;

import java.math.BigDecimal;

public record RecipeItemResponse(
        Long id,
        Long menuItemId,
        Long productId,
        String productName,
        ProductUnit unit,
        BigDecimal requiredQuantity
) {
}
