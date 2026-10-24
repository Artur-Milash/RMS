package com.rms.restaurant_management_system.reservation.controller;

import com.rms.restaurant_management_system.reservation.dto.ReservationCreateRequest;
import com.rms.restaurant_management_system.reservation.dto.ReservationResponse;
import com.rms.restaurant_management_system.reservation.dto.ReservationUpdateRequest;
import com.rms.restaurant_management_system.reservation.model.Reservation;
import com.rms.restaurant_management_system.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public List<ReservationResponse> getAll() {
        return reservationService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ReservationResponse getById(@PathVariable Long id) {
        return toResponse(reservationService.getReservation(id));
    }

    @GetMapping("/table/{tableId}")
    public List<ReservationResponse> getByTable(@PathVariable Long tableId) {
        return reservationService.getTableReservations(tableId).stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(@Valid @RequestBody ReservationCreateRequest request) {
        Reservation reservation = reservationService.createReservation(request.tableId(), request.customerName(),
                request.customerPhone(), request.reservationTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(reservation));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ReservationResponse update(@PathVariable Long id, @Valid @RequestBody ReservationUpdateRequest request) {
        return toResponse(reservationService.update(id, request));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@PathVariable Long id) {
        reservationService.completeReservation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        reservationService.cancelReservation(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(reservation.getId(), reservation.getTable().getId(),
                reservation.getTable().getNumber(), reservation.getCustomerName(), reservation.getCustomerPhone(),
                reservation.getReservationTime(), reservation.getStatus());
    }
}