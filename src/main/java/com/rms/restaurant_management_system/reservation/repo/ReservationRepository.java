package com.rms.restaurant_management_system.reservation.repo;

import com.rms.restaurant_management_system.reservation.model.Reservation;
import com.rms.restaurant_management_system.reservation.model.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationRepository
        extends JpaRepository<Reservation, Long> {

    List<Reservation> findByTableId(Long tableId);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByReservationTimeBetween(
            LocalDateTime from,
            LocalDateTime to
    );

    List<Reservation> findByTableIdAndReservationTimeBetween(
            Long tableId,
            LocalDateTime from,
            LocalDateTime to
    );
}