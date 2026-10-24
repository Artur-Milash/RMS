package com.rms.restaurant_management_system.order.order_item.controller;

import com.rms.restaurant_management_system.order.order_item.dto.OrderItemCreateRequest;
import com.rms.restaurant_management_system.order.order_item.dto.OrderItemResponse;
import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.service.OrderItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/order-items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemService orderItemService;

    @GetMapping("/preparing")
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public List<OrderItemResponse> getPreparing() {
        return orderItemService.getPreparingItems().stream().map(this::toResponse).toList();
    }

    @GetMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<OrderItemResponse> getByOrder(@PathVariable Long orderId) {
        return orderItemService.getOrderItems(orderId).stream().map(this::toResponse).toList();
    }

    @PostMapping("/order/{orderId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public ResponseEntity<OrderItemResponse> create(
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemCreateRequest request) {
        OrderItem item = orderItemService.addItem(orderId, request.menuItemId(), request.quantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(item));
    }

    @PostMapping("/{id}/ready")
    @PreAuthorize("hasAnyRole('MANAGER', 'CHEF')")
    public ResponseEntity<Void> markReady(@PathVariable Long id) {
        orderItemService.markAsReady(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/served")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public ResponseEntity<Void> markServed(@PathVariable Long id) {
        orderItemService.markAsServed(id);
        return ResponseEntity.noContent().build();
    }

    private OrderItemResponse toResponse(OrderItem item) {
        BigDecimal totalPrice = item.getPriceAtOrder().multiply(BigDecimal.valueOf(item.getQuantity()));
        return new OrderItemResponse(item.getId(), item.getMenuItem().getId(), item.getMenuItem().getName(),
                item.getQuantity(), item.getStatus(), item.getPriceAtOrder(), totalPrice);
    }
}