package com.rms.restaurant_management_system.supply.controller;

import com.rms.restaurant_management_system.supply.dto.SupplyCreateRequest;
import com.rms.restaurant_management_system.supply.dto.SupplyResponse;
import com.rms.restaurant_management_system.supply.model.Supply;
import com.rms.restaurant_management_system.supply.service.SupplyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supplies")
@RequiredArgsConstructor
public class SupplyController {

    private final SupplyService supplyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public List<SupplyResponse> getAll() {
        return supplyService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public List<SupplyResponse> getByProduct(@PathVariable Long productId) {
        return supplyService.getByProduct(productId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public List<SupplyResponse> getByUser(@PathVariable Long userId) {
        return supplyService.getByUser(userId).stream().map(this::toResponse).toList();
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public ResponseEntity<SupplyResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody SupplyCreateRequest request) {
        Long userId = jwt.getClaim("userId");
        Supply supply = supplyService.addSupply(request.productId(), userId, request.addedQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(supply));
    }

    private SupplyResponse toResponse(Supply supply) {
        return new SupplyResponse(supply.getId(), supply.getProduct().getId(), supply.getProduct().getName(),
                supply.getUser().getId(), supply.getUser().getFullName(), supply.getAddedQuantity(), supply.getSupplyDate());
    }
}