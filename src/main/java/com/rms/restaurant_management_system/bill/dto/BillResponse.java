package com.rms.restaurant_management_system.bill.dto;

import com.rms.restaurant_management_system.bill.model.BillStatus;
import com.rms.restaurant_management_system.bill.model.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillResponse(
        Long id,
        Long orderId,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        LocalDateTime paymentTime,
        BillStatus status
) {
}
