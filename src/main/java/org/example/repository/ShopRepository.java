package org.example.repository;

import org.example.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for Shop entity CRUD operations.
 * Extends JpaRepository to provide standard database operations for Shop entities.
 * This interface automatically gets implemented by Spring Data JPA at runtime.
 */
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // No additional methods needed - inherits all standard CRUD operations from JpaRepository
    // including: save(), findById(), findAll(), deleteById(), count(), etc.
}