package com.rms.restaurant_management_system.order.order_item.repo;

import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.model.OrderItemStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository
        extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByMenuItemId(Long menuItemId);

    List<OrderItem> findByStatus(OrderItemStatus status);

    List<OrderItem> findByOrderIdAndStatus(
            Long orderId,
            OrderItemStatus status
    );
}