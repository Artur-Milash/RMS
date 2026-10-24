package com.rms.restaurant_management_system.bill.dto;

import com.rms.restaurant_management_system.bill.model.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record BillCreateRequest(

        @NotNull
        Long orderId,

        @NotNull
        PaymentMethod paymentMethod
) {
}

