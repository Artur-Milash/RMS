package com.rms.restaurant_management_system.recipe_item.controller;

import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.recipe_item.dto.RecipeItemCreateRequest;
import com.rms.restaurant_management_system.recipe_item.dto.RecipeItemResponse;
import com.rms.restaurant_management_system.recipe_item.dto.RecipeItemUpdateRequest;
import com.rms.restaurant_management_system.recipe_item.model.RecipeItem;
import com.rms.restaurant_management_system.recipe_item.service.RecipeItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipe-items")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MANAGER')")
public class RecipeItemController {

    private final RecipeItemService recipeItemService;

    @GetMapping
    public List<RecipeItemResponse> getAll() {
        return recipeItemService.getAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/menu/{menuItemId}")
    public List<RecipeItemResponse> getByMenuItem(@PathVariable Long menuItemId) {
        return recipeItemService.getByMenuItem(menuItemId).stream().map(this::toResponse).toList();
    }

    @PostMapping
    public ResponseEntity<RecipeItemResponse> create(@Valid @RequestBody RecipeItemCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(toResponse(recipeItemService.create(request)));
    }

    @PutMapping("/{id}")
    public RecipeItemResponse update(@PathVariable Long id, @Valid @RequestBody RecipeItemUpdateRequest request) {
        return toResponse(recipeItemService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        recipeItemService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private RecipeItemResponse toResponse(RecipeItem item) {
        Product product = item.getProduct();
        return new RecipeItemResponse(item.getId(), item.getMenuItem().getId(), product.getId(),
                product.getName(), product.getUnit(), item.getRequiredQuantity());
    }
}