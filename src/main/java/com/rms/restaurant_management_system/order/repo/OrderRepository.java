package com.rms.restaurant_management_system.order.repo;

import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository
        extends JpaRepository<Order, Long> {

    @EntityGraph(attributePaths = {"table", "waiter"})
    @Override
    Page<Order> findAll(Pageable pageable);

    List<Order> findByTableId(Long tableId);

    @EntityGraph(attributePaths = {"table", "waiter"})
    List<Order> findByWaiterId(Long waiterId);

    @EntityGraph(attributePaths = {"table", "waiter"})
    List<Order> findByStatus(OrderStatus status);

    List<Order> findByTableIdAndStatus(
            Long tableId,
            OrderStatus status
    );
}