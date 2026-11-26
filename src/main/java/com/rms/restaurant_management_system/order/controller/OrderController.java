package com.rms.restaurant_management_system.order.controller;

import com.rms.restaurant_management_system.order.dto.OrderCreateRequest;
import com.rms.restaurant_management_system.order.dto.OrderResponse;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.service.OrderService;
import com.rms.restaurant_management_system.order.order_item.dto.OrderItemResponse;
import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.service.OrderItemService;
import com.rms.restaurant_management_system.common.exception.InvalidQuantityException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Profile({"all", "orders"})
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final int DEFAULT_PAGE_SIZE = 100;
    private static final int MAX_PAGE_SIZE = 200;

    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public ResponseEntity<List<OrderResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        if (page < 0 || size < 1 || size > MAX_PAGE_SIZE) {
            throw new InvalidQuantityException("page must be non-negative and size must be between 1 and 200");
        }

        Page<Order> orderPage = orderService.getAll(PageRequest.of(page, size));
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Total-Count", Long.toString(orderPage.getTotalElements()));
        headers.set("X-Total-Pages", Integer.toString(orderPage.getTotalPages()));
        headers.set("X-Page", Integer.toString(orderPage.getNumber()));
        headers.set("X-Page-Size", Integer.toString(orderPage.getSize()));
        if (orderPage.hasNext()) {
            headers.add(HttpHeaders.LINK, pageLink(orderPage.getNumber() + 1, orderPage.getSize(), "next"));
        }
        if (orderPage.hasPrevious()) {
            headers.add(HttpHeaders.LINK, pageLink(orderPage.getNumber() - 1, orderPage.getSize(), "prev"));
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(toResponses(orderPage.getContent()));
    }

    @GetMapping("/open")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER', 'CHEF')")
    public List<OrderResponse> getOpen() {
        return toResponses(orderService.getOpenOrders());
    }

    @GetMapping("/waiter/{waiterId}")
    @PreAuthorize("hasAnyRole('MANAGER', 'WAITER')")
    public List<OrderResponse> getByWaiter(@PathVariable Long waiterId) {
        return toResponses(orderService.getWaiterOrders(waiterId));
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
        return toResponse(order, orderItemService.getOrderItems(order.getId()));
    }

    private List<OrderResponse> toResponses(List<Order> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }

        Map<Long, List<OrderItem>> itemsByOrderId = orderItemService.getOrderItemsByOrderIds(
                        orders.stream().map(Order::getId).toList())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getOrder().getId()));

        return orders.stream()
                .map(order -> toResponse(order, itemsByOrderId.getOrDefault(order.getId(), List.of())))
                .toList();
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(this::toItemResponse)
                .toList();
        return new OrderResponse(order.getId(), order.getTable().getId(), order.getTable().getNumber(),
                order.getWaiter().getId(), order.getWaiter().getFullName(), order.getCreatedAt(), order.getStatus(),
                itemResponses);
    }

    private String pageLink(int page, int size, String relation) {
        String uri = UriComponentsBuilder.fromPath("/api/orders")
                .queryParam("page", page)
                .queryParam("size", size)
                .build()
                .toUriString();
        return "<" + uri + ">; rel=\"" + relation + "\"";
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(item.getId(), item.getMenuItem().getId(), item.getMenuItem().getName(),
                item.getQuantity(), item.getStatus(), item.getPriceAtOrder(),
                item.getPriceAtOrder().multiply(java.math.BigDecimal.valueOf(item.getQuantity())));
    }
}