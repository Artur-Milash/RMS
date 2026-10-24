package com.rms.restaurant_management_system.restaurant_table.repo;

import com.rms.restaurant_management_system.restaurant_table.model.RestaurantTable;
import com.rms.restaurant_management_system.restaurant_table.model.TableStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<RestaurantTable, Long> {

    Optional<RestaurantTable> findByNumber(Integer number);

    boolean existsByNumber(Integer number);

    List<RestaurantTable> findByStatus(TableStatus status);
}