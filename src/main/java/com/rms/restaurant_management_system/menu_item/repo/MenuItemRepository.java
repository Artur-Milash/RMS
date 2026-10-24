package com.rms.restaurant_management_system.menu_item.repo;

import com.rms.restaurant_management_system.menu_item.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository
        extends JpaRepository<MenuItem, Long> {

    List<MenuItem> findByActiveTrue();

    List<MenuItem> findByActiveFalse();
}