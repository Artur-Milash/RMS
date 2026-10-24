package com.rms.restaurant_management_system.reservation.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.InvalidStateException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.reservation.dto.ReservationUpdateRequest;
import com.rms.restaurant_management_system.reservation.model.Reservation;
import com.rms.restaurant_management_system.reservation.model.ReservationStatus;
import com.rms.restaurant_management_system.reservation.repo.ReservationRepository;
import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.restaurant_table.repo.TableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final TableRepository tableRepository;

    @Transactional(readOnly = true)
    public List<Reservation> getAll() {
        return reservationRepository.findAll();
    }

    @Transactional
    public Reservation createReservation(
            Long tableId,
            String customerName,
            String customerPhone,
            LocalDateTime reservationTime
    ) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        List<Reservation> reservations =
                reservationRepository.findByTableIdAndReservationTimeBetween(
                        tableId,
                        reservationTime.minusHours(2),
                        reservationTime.plusHours(2)
                );

        boolean alreadyReserved = reservations.stream()
                .anyMatch(reservation ->
                        reservation.getStatus() == ReservationStatus.PENDING
                );

        if (alreadyReserved) {
            throw new BusinessConflictException("Table is already reserved");
        }

        Reservation reservation = Reservation.builder()
                .table(table)
                .customerName(customerName)
                .customerPhone(customerPhone)
                .reservationTime(reservationTime)
                .status(ReservationStatus.PENDING)
                .build();

        return reservationRepository.save(reservation);
    }

    @Transactional(readOnly = true)
    public Reservation getReservation(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));
    }

    @Transactional(readOnly = true)
    public List<Reservation> getTableReservations(Long tableId) {
        return reservationRepository.findByTableId(tableId);
    }

    @Transactional
    public Reservation update(Long id, ReservationUpdateRequest request) {
        Reservation reservation = getReservation(id);
        RestaurantTable table = tableRepository.findById(request.tableId())
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));
        reservation.setTable(table);
        reservation.setCustomerName(request.customerName());
        reservation.setCustomerPhone(request.customerPhone());
        reservation.setReservationTime(request.reservationTime());
        reservation.setStatus(request.status());
        return reservationRepository.save(reservation);
    }

    @Transactional
    public void delete(Long id) {
        reservationRepository.delete(getReservation(id));
    }

    @Transactional
    public void cancelReservation(Long id) {
        Reservation reservation = getReservation(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidStateException("Reservation cannot be cancelled");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
    }

    @Transactional
    public void completeReservation(Long id) {
        Reservation reservation = getReservation(id);

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new InvalidStateException("Reservation cannot be completed");
        }

        reservation.setStatus(ReservationStatus.COMPLETED);
    }
}
