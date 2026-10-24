package com.rms.restaurant_management_system.order.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.ForbiddenOperationException;
import com.rms.restaurant_management_system.common.exception.InvalidStateException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import com.rms.restaurant_management_system.order.order_item.dto.OrderItemCreateRequest;
import com.rms.restaurant_management_system.order.order_item.service.OrderItemService;
import com.rms.restaurant_management_system.order.repo.OrderRepository;
import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.restaurant_table.repo.TableRepository;
import com.rms.restaurant_management_system.user.model.User;
import com.rms.restaurant_management_system.user.model.UserRole;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final UserRepository userRepository;
    private final OrderItemService orderItemService;

    @Transactional(readOnly = true)
    public List<Order> getAll() {
        return orderRepository.findAll();
    }

    @Transactional
    public Order createOrder(Long tableId, Long waiterId) {

        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found"));

        User waiter = userRepository.findById(waiterId)
                .orElseThrow(() -> new ResourceNotFoundException("Waiter not found"));

        if (!waiter.isActive()) {
            throw new ForbiddenOperationException("User is not active");
        }

        if (waiter.getRole() != UserRole.WAITER) {
            throw new ForbiddenOperationException("User is not a waiter");
        }

        List<Order> openOrders =
                orderRepository.findByTableIdAndStatus(
                        tableId,
                        OrderStatus.OPEN
                );

        if (!openOrders.isEmpty()) {
            throw new BusinessConflictException("Table already has an open order");
        }

        Order order = Order.builder()
                .table(table)
                .waiter(waiter)
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.OPEN)
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public Order createOrder(Long tableId, Long waiterId, List<OrderItemCreateRequest> items) {
        Order order = createOrder(tableId, waiterId);
        for (OrderItemCreateRequest item : items) {
            orderItemService.addItem(order.getId(), item.menuItemId(), item.quantity());
        }
        return order;
    }

    @Transactional(readOnly = true)
    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    @Transactional(readOnly = true)
    public List<Order> getOpenOrders() {
        return orderRepository.findByStatus(OrderStatus.OPEN);
    }

    @Transactional(readOnly = true)
    public List<Order> getWaiterOrders(Long waiterId) {
        return orderRepository.findByWaiterId(waiterId);
    }

    @Transactional
    public void closeOrder(Long id) {
        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new InvalidStateException("Order is not open");
        }

        order.setStatus(OrderStatus.CLOSED);
    }

    @Transactional
    public void cancelOrder(Long id) {
        Order order = getOrder(id);

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new InvalidStateException("Order cannot be cancelled");
        }

        order.setStatus(OrderStatus.CANCELLED);
    }
}