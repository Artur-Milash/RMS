package com.rms.restaurant_management_system.reservation.dto;

import com.rms.restaurant_management_system.reservation.model.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long tableId,
        Integer tableNumber,
        String customerName,
        String customerPhone,
        LocalDateTime reservationTime,
        ReservationStatus status
) {
}
