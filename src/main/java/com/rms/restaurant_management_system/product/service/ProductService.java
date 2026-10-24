package com.rms.restaurant_management_system.product.service;

import com.rms.restaurant_management_system.common.exception.BusinessConflictException;
import com.rms.restaurant_management_system.common.exception.ResourceNotFoundException;
import com.rms.restaurant_management_system.product.dto.ProductCreateRequest;
import com.rms.restaurant_management_system.product.dto.ProductUpdateRequest;
import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.product.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Product getById(Long id) {
        return findProduct(id);
    }

    @Transactional
    public Product create(ProductCreateRequest request) {
        if (productRepository.existsByName(request.name())) {
            throw new BusinessConflictException("Product name already exists");
        }

        Product product = Product.builder()
                .name(request.name())
                .unit(request.unit())
                .stockQuantity(request.stockQuantity())
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product update(Long id, ProductUpdateRequest request) {
        Product product = findProduct(id);
        productRepository.findByName(request.name())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new BusinessConflictException("Product name already exists");
                });

        product.setName(request.name());
        product.setUnit(request.unit());
        product.setStockQuantity(request.stockQuantity());
        return productRepository.save(product);
    }

    @Transactional
    public void delete(Long id) {
        productRepository.delete(findProduct(id));
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }
}