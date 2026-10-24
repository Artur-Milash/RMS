package com.rms.restaurant_management_system.order.controller;

import com.rms.restaurant_management_system.order.dto.OrderCreateRequest;
import com.rms.restaurant_management_system.order.dto.OrderResponse;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.service.OrderService;
import com.rms.restaurant_management_system.order.order_item.dto.OrderItemResponse;
import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.service.OrderItemService;
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
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<OrderResponse> getAll() {
        return orderService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<OrderResponse> getOpen() {
        return orderService.getOpenOrders().stream().map(this::toResponse).toList();
    }

    @GetMapping("/waiter/{waiterId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public List<OrderResponse> getByWaiter(@PathVariable Long waiterId) {
        return orderService.getWaiterOrders(waiterId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public OrderResponse getById(@PathVariable Long id) {
        return toResponse(orderService.getOrder(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('WAITER')")
    public ResponseEntity<OrderResponse> create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody OrderCreateRequest request) {
        Long waiterId = jwt.getClaim("userId");
        Order order = orderService.createOrder(request.tableId(), waiterId, request.items());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(order));
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public ResponseEntity<Void> close(@PathVariable Long id) {
        orderService.closeOrder(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    private OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = orderItemService.getOrderItems(order.getId()).stream()
                .map(this::toItemResponse)
                .toList();
        return new OrderResponse(order.getId(), order.getTable().getId(), order.getTable().getNumber(),
                order.getWaiter().getId(), order.getWaiter().getFullName(), order.getCreatedAt(), order.getStatus(), items);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getMenuItem().getId(), item.getMenuItem().getName(),
                item.getQuantity(), item.getStatus(), item.getPriceAtOrder(),
                item.getPriceAtOrder().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
    }
}