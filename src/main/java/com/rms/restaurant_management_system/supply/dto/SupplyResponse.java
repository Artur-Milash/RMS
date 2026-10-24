package com.rms.restaurant_management_system.supply.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SupplyResponse(
        Long id,
        Long productId,
        String productName,
        Long userId,
        String userName,
        BigDecimal addedQuantity,
        LocalDateTime supplyDate
) {
}
