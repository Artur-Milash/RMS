package com.rms.restaurant_management_system.product.dto;

import com.rms.restaurant_management_system.product.model.ProductUnit;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ProductCreateRequest(

        @NotBlank
        String name,

        @NotNull
        ProductUnit unit,

        @NotNull
        @DecimalMin(value = "0.0")
        BigDecimal stockQuantity
) {
}