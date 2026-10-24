package com.rms.restaurant_management_system.recipe_item.repo;

import com.rms.restaurant_management_system.recipe_item.model.RecipeItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecipeItemRepository
        extends JpaRepository<RecipeItem, Long> {

    List<RecipeItem> findByMenuItemId(Long menuItemId);

    List<RecipeItem> findByProductId(Long productId);

    boolean existsByMenuItemIdAndProductId(
            Long menuItemId,
            Long productId
    );
}
