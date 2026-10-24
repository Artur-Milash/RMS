package com.rms.restaurant_management_system.bill.repo;

import com.rms.restaurant_management_system.bill.model.Bill;
import com.rms.restaurant_management_system.bill.model.BillStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillRepository
        extends JpaRepository<Bill, Long> {

    Optional<Bill> findByOrderId(Long orderId);

    boolean existsByOrderId(Long orderId);

    Optional<Bill> findByOrderIdAndStatus(
            Long orderId,
            BillStatus status
    );
}
