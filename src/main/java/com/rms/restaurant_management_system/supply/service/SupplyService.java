package com.rms.restaurant_management_system.supply.service;
import com.rms.restaurant_management_system.common.exception.ForbiddenOperationException;
import com.rms.restaurant_management_system.common.exception.InvalidQuantityException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.product.repo.ProductRepository;
import com.rms.restaurant_management_system.supply.model.Supply;
import com.rms.restaurant_management_system.supply.repo.SupplyRepository;
import com.rms.restaurant_management_system.user.model.User;
import com.rms.restaurant_management_system.user.model.UserRole;
import com.rms.restaurant_management_system.user.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplyService {

    private final SupplyRepository supplyRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

        @Transactional(readOnly = true)
        public List<Supply> getAll() {
                return supplyRepository.findAll();
        }

        @Transactional(readOnly = true)
        public List<Supply> getByProduct(Long productId) {
                return supplyRepository.findByProductId(productId);
        }

        @Transactional(readOnly = true)
        public List<Supply> getByUser(Long userId) {
                return supplyRepository.findByUserId(userId);
        }

    @Transactional
    public Supply addSupply(
            Long productId,
            Long userId,
            BigDecimal quantity
    ) {
        if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidQuantityException("Quantity must be positive");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!user.isActive()) {
            throw new ForbiddenOperationException("User is not active");
        }

        if (user.getRole() != UserRole.MANAGER
                && user.getRole() != UserRole.CHEF) {
            throw new ForbiddenOperationException(
                    "Only manager or chef can add supplies"
            );
        }

        product.setStockQuantity(
                product.getStockQuantity().add(quantity)
        );

        Supply supply = Supply.builder()
                .product(product)
                .user(user)
                .addedQuantity(quantity)
                .supplyDate(LocalDateTime.now())
                .build();

        productRepository.save(product);

        return supplyRepository.save(supply);
    }
}
