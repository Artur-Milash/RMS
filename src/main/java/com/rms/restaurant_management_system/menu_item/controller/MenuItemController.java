package com.rms.restaurant_management_system.menu_item.controller;

import com.rms.restaurant_management_system.menu_item.dto.MenuItemCreateRequest;
import com.rms.restaurant_management_system.menu_item.dto.MenuItemResponse;
import com.rms.restaurant_management_system.menu_item.model.MenuItem;
import com.rms.restaurant_management_system.menu_item.service.MenuService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu-items")
@RequiredArgsConstructor
public class MenuItemController {

    private final MenuService menuService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<MenuItemResponse> getAll() {
        return menuService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/available")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<MenuItemResponse> getAvailable() {
        return menuService.getAvailableMenu().stream()
                .map(item -> toResponse(item, true))
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public MenuItemResponse getById(@PathVariable Long id) {
        return toResponse(menuService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<MenuItemResponse> create(@Valid @RequestBody MenuItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(menuService.create(request)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public MenuItemResponse update(@PathVariable Long id, @Valid @RequestBody MenuItemCreateRequest request) {
        return toResponse(menuService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        menuService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private MenuItemResponse toResponse(MenuItem item) {
        return toResponse(item, menuService.isAvailable(item.getId()));
    }

    private MenuItemResponse toResponse(MenuItem item, boolean available) {
        return new MenuItemResponse(item.getId(), item.getName(), item.getDescription(), item.getPrice(),
                item.isActive(), item.isActive() && available);
    }
}