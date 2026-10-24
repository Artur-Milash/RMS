package com.rms.restaurant_management_system.recipe_item.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.menu_item.model.MenuItem;
import com.rms.restaurant_management_system.menu_item.repo.MenuItemRepository;
import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.product.repo.ProductRepository;
import com.rms.restaurant_management_system.recipe_item.dto.RecipeItemCreateRequest;
import com.rms.restaurant_management_system.recipe_item.dto.RecipeItemUpdateRequest;
import com.rms.restaurant_management_system.recipe_item.model.RecipeItem;
import com.rms.restaurant_management_system.recipe_item.repo.RecipeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeItemService {

    private final RecipeItemRepository recipeItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<RecipeItem> getAll() {
        return recipeItemRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<RecipeItem> getByMenuItem(Long menuItemId) {
        findMenuItem(menuItemId);
        return recipeItemRepository.findByMenuItemId(menuItemId);
    }

    @Transactional
    public RecipeItem create(RecipeItemCreateRequest request) {
        MenuItem menuItem = findMenuItem(request.menuItemId());
        Product product = findProduct(request.productId());

        if (recipeItemRepository.existsByMenuItemIdAndProductId(menuItem.getId(), product.getId())) {
            throw new BusinessConflictException("Product is already part of this menu item's recipe");
        }

        RecipeItem recipeItem = RecipeItem.builder()
                .menuItem(menuItem)
                .product(product)
                .requiredQuantity(request.requiredQuantity())
                .build();
        return recipeItemRepository.save(recipeItem);
    }

    @Transactional
    public RecipeItem update(Long id, RecipeItemUpdateRequest request) {
        RecipeItem recipeItem = findRecipeItem(id);
        Product product = findProduct(request.productId());

        boolean productAlreadyUsed = recipeItemRepository
                .existsByMenuItemIdAndProductId(recipeItem.getMenuItem().getId(), product.getId());
        if (productAlreadyUsed && !recipeItem.getProduct().getId().equals(product.getId())) {
            throw new BusinessConflictException("Product is already part of this menu item's recipe");
        }

        recipeItem.setProduct(product);
        recipeItem.setRequiredQuantity(request.requiredQuantity());
        return recipeItemRepository.save(recipeItem);
    }

    @Transactional
    public void delete(Long id) {
        recipeItemRepository.delete(findRecipeItem(id));
    }

    private RecipeItem findRecipeItem(Long id) {
        return recipeItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe item not found"));
    }

    private MenuItem findMenuItem(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}