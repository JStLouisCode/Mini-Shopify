package org.example.repository;

import org.example.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByShopId(Long shopId);
    List<Product> findByProductNameContainingIgnoreCase(String productName);
    List<Product> findByProductInventoryGreaterThan(Integer inventory);
}