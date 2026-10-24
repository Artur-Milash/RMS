package com.rms.restaurant_management_system.order.order_item.service;

import com.rms.restaurant_management_system.common.exception.InsufficientStockException;
import com.rms.restaurant_management_system.common.exception.InvalidQuantityException;
import com.rms.restaurant_management_system.common.exception.InvalidStateException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.menu_item.model.MenuItem;
import com.rms.restaurant_management_system.menu_item.repo.MenuItemRepository;
import com.rms.restaurant_management_system.order.model.Order;
import com.rms.restaurant_management_system.order.model.OrderStatus;
import com.rms.restaurant_management_system.order.order_item.model.OrderItem;
import com.rms.restaurant_management_system.order.order_item.model.OrderItemStatus;
import com.rms.restaurant_management_system.order.order_item.repo.OrderItemRepository;
import com.rms.restaurant_management_system.order.repo.OrderRepository;
import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.product.repo.ProductRepository;
import com.rms.restaurant_management_system.recipe_item.model.RecipeItem;
import com.rms.restaurant_management_system.recipe_item.repo.RecipeItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final RecipeItemRepository recipeItemRepository;
    private final ProductRepository productRepository;

    @Transactional
    public OrderItem addItem(
            Long orderId,
            Long menuItemId,
            int quantity
    ) {
        if (quantity <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new InvalidStateException("Order is not open");
        }

        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found"));

        if (!menuItem.isActive()) {
            throw new InvalidStateException("Menu item is not active");
        }

        List<RecipeItem> recipe =
                recipeItemRepository.findByMenuItemId(menuItemId);

        checkIngredients(recipe, quantity);

        decreaseStock(recipe, quantity);

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(quantity)
                .status(OrderItemStatus.PREPARING)
                .priceAtOrder(menuItem.getPrice())
                .build();

        return orderItemRepository.save(orderItem);
    }

    private void checkIngredients(
            List<RecipeItem> recipe,
            int quantity
    ) {
        for (RecipeItem recipeItem : recipe) {

            Product product = recipeItem.getProduct();

            BigDecimal required =
                    recipeItem.getRequiredQuantity()
                            .multiply(BigDecimal.valueOf(quantity));

            if (product.getStockQuantity().compareTo(required) < 0) {
                throw new InsufficientStockException(
                        "Not enough product: " + product.getName()
                );
            }
        }
    }

    private void decreaseStock(
            List<RecipeItem> recipe,
            int quantity
    ) {
        for (RecipeItem recipeItem : recipe) {

            Product product = recipeItem.getProduct();

            BigDecimal required =
                    recipeItem.getRequiredQuantity()
                            .multiply(BigDecimal.valueOf(quantity));

            product.setStockQuantity(
                    product.getStockQuantity().subtract(required)
            );

            productRepository.save(product);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Transactional(readOnly = true)
    public List<OrderItem> getPreparingItems() {
        return orderItemRepository.findByStatus(OrderItemStatus.PREPARING);
    }

    @Transactional
    public void markAsReady(Long orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (item.getStatus() != OrderItemStatus.PREPARING) {
            throw new InvalidStateException("Item is not being prepared");
        }

        item.setStatus(OrderItemStatus.READY);
    }

    @Transactional
    public void markAsServed(Long orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Order item not found"));

        if (item.getStatus() != OrderItemStatus.READY) {
            throw new InvalidStateException("Item is not ready");
        }

        item.setStatus(OrderItemStatus.SERVED);
    }
}