package com.rms.restaurant_management_system.menu_item.service;

import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.menu_item.dto.MenuItemCreateRequest;
import com.rms.restaurant_management_system.menu_item.model.MenuItem;
import com.rms.restaurant_management_system.menu_item.repo.MenuItemRepository;
import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.recipe_item.model.RecipeItem;
import com.rms.restaurant_management_system.recipe_item.repo.RecipeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuItemRepository menuItemRepository;
    private final RecipeItemRepository recipeItemRepository;

    @Transactional(readOnly = true)
    public List<MenuItem> getAvailableMenu() {

        return menuItemRepository.findByActiveTrue()
                .stream()
                .filter(this::isAvailable)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MenuItem> getAll() {
        return menuItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MenuItem getById(Long id) {
        return findMenuItem(id);
    }

    @Transactional
    public MenuItem create(MenuItemCreateRequest request) {
        MenuItem menuItem = MenuItem.builder()
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .active(request.active())
                .build();
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem update(Long id, MenuItemCreateRequest request) {
        MenuItem menuItem = findMenuItem(id);
        menuItem.setName(request.name());
        menuItem.setDescription(request.description());
        menuItem.setPrice(request.price());
        menuItem.setActive(request.active());
        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public void delete(Long id) {
        menuItemRepository.delete(findMenuItem(id));
    }

    @Transactional(readOnly = true)
    public boolean isAvailable(Long menuItemId) {

        MenuItem menuItem = findMenuItem(menuItemId);

        return menuItem.isActive() && isAvailable(menuItem);
    }

    private MenuItem findMenuItem(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }

    private boolean isAvailable(MenuItem menuItem) {

        List<RecipeItem> recipe =
                recipeItemRepository.findByMenuItemId(menuItem.getId());

        for (RecipeItem item : recipe) {

            Product product = item.getProduct();

            if (product.getStockQuantity()
                    .compareTo(item.getRequiredQuantity()) < 0) {

                return false;
            }
        }

        return true;
    }
}