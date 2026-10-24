package com.rms.restaurant_management_system.reservation.dto;

import com.rms.restaurant_management_system.reservation.model.ReservationStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ReservationUpdateRequest(

        @NotNull
        Long tableId,

        @NotBlank
        String customerName,

        @NotBlank
        String customerPhone,

        @NotNull
        @Future
        LocalDateTime reservationTime,

        @NotNull
        ReservationStatus status
) {
}