package org.example;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;

/**
 * Facade for managing user-related operations in the Mini-Shopify system.
 * This current implementation is a stub and not the final implementation.
 * It is only here to provide enough functionality so the shop management features can work.
 */
@Service
public class UserManagementFacade {

    private int id; // User ID

    private String name; // User name

    private ShopManagementFacade shop; // Associated shop management facade

    /**
     * Sets the user ID.
     * @param id the user ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Sets the user name.
     * @param name the user name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the associated shop management facade.
     * @return the shop management facade
     */
    public ShopManagementFacade getShop() {
        return shop;
    }

    /**
     * Default constructor. Initializes with a default name.
     */
    public UserManagementFacade() {
        this.name = "Mr. Owner"; // Default user name
    }

    /**
     * Sets the shop management facade for this user.
     * @param shop the shop management facade to associate
     */
    public void setShop(ShopManagementFacade shop) {
        this.shop = shop;
    }

    /**
     * Gets the user ID.
     * @return the user ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the user name.
     * @return the user name
     */
    public String getName() {
        return name;
    }
}