package org.example.repository;

import org.example.model.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Shop entity CRUD operations.
 * Extends JpaRepository to provide standard database operations for Shop entities.
 * This interface automatically gets implemented by Spring Data JPA at runtime.
 */
public interface ShopRepository extends JpaRepository<Shop, Long> {
    // No additional methods needed - inherits all standard CRUD operations from JpaRepository
    // including: save(), findById(), findAll(), deleteById(), count(), etc.

    Optional<Shop> findByName(String newFormShop); // used for a test


    /**
     * Finds shops by name (case-insensitive partial match).
     * Uses Spring Data JPA query derivation.
     *
     * @param name the name to search for (partial match)
     * @return list of shops with names containing the search term
     */
    List<Shop> findByNameContainingIgnoreCase(String name);


    /**
     * Finds shops that have a specific tag.
     * Uses a custom JPQL query because tags are stored in an @ElementCollection.
     *
     * @param tag the tag to search for
     * @return list of shops containing the specified tag
     */
    @Query("SELECT s FROM Shop s JOIN s.tags t WHERE LOWER(t) = LOWER(:tag)")
    List<Shop> findByTagsContainingIgnoreCase(@Param("tag") String tag);
}