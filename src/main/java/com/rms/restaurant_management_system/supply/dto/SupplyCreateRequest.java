package com.rms.restaurant_management_system.supply.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SupplyCreateRequest(

        @NotNull
        Long productId,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal addedQuantity
) {
}
