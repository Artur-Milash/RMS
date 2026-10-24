package com.rms.restaurant_management_system.restaurant_table.controller;

import com.rms.restaurant_management_system.restaurant_table.dto.TableCreateRequest;
import com.rms.restaurant_management_system.restaurant_table.dto.TableResponse;
import com.rms.restaurant_management_system.restaurant_table.dto.TableUpdateRequest;
import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.restaurant_table.service.RestaurantTableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tables")
@RequiredArgsConstructor
public class TableController {

    private final RestaurantTableService tableService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public List<TableResponse> getAll() {
        return tableService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public List<TableResponse> getAvailable() {
        return tableService.getAvailable().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public TableResponse getById(@PathVariable Long id) {
        return toResponse(tableService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TableResponse> create(@Valid @RequestBody TableCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(tableService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public TableResponse update(@PathVariable Long id, @Valid @RequestBody TableUpdateRequest request) {
        return toResponse(tableService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private TableResponse toResponse(RestaurantTable table) {
        return new TableResponse(table.getId(), table.getNumber(), table.getSeatsCount(), table.getStatus());
    }
}