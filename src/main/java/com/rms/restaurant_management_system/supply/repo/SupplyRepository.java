package com.rms.restaurant_management_system.supply.repo;

import com.rms.restaurant_management_system.supply.model.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SupplyRepository
        extends JpaRepository<Supply, Long> {

    List<Supply> findByProductId(Long productId);

    List<Supply> findByUserId(Long userId);

    List<Supply> findBySupplyDateBetween(
            LocalDateTime from,
            LocalDateTime to
    );
}
