package com.rms.restaurant_management_system.supply.model;

import com.rms.restaurant_management_system.product.model.Product;
import com.rms.restaurant_management_system.user.model.User;
import jakarta.persistence.*;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "supplies")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Supply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "added_quantity", nullable = false)
    private BigDecimal addedQuantity;

    @Column(name = "supply_date", nullable = false)
    private LocalDateTime supplyDate;
}

