package com.rms.restaurant_management_system.recipe_item.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RecipeItemUpdateRequest(

        @NotNull
        Long productId,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal requiredQuantity
) {
}
