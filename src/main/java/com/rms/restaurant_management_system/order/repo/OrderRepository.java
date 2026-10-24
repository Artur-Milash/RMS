package com.rms.restaurant_management_system.order.repo;

import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    List<Order> findByTableId(Long tableId);

    List<Order> findByWaiterId(Long waiterId);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByTableIdAndStatus(
            Long tableId,
            OrderStatus status
    );
}