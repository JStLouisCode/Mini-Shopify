package org.example;

import org.example.model.Shop; // Import the Shop DATA entity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List; // Use List interface

@Service
public class ShopManagementFacade {

    @Autowired
    private ProductManagementFacade productManager;

    @Autowired
    private UserManagementFacade userManager; // Assuming this is also a @Service

    @Autowired
    private OrderAndCheckoutFacade orderer;

    // --- 3. ADD THE NO-ARG CONSTRUCTOR ---
    // This is what Spring needs to create the bean.
    public ShopManagementFacade() {}

    // --- 4. UPDATE METHODS TO USE A 'Shop' PARAMETER ---
    // This facade can now manage ANY shop, not just one.

    public String getName(Shop shop) {
        return shop.getName();
    }

    public void setName(Shop shop, String name) {
        shop.setName(name);
        // In a real app, you would save the shop entity here
        // shopRepository.save(shop);
    }

    public List<String> getTags(Shop shop) { // Use List
        return shop.getTags();
    }

    public void setTags(Shop shop, ArrayList<String> tags) {
        shop.setTags(tags);
    }

    public String getShopType(Shop shop) {
        return shop.getBusinessType(); // Assumes 'getBusinessType' exists in Shop.java
    }

    public void setShopType(Shop shop, String shopType) {
        shop.setBusinessType(shopType);
    }

    // These methods now just return the injected services
    public ProductManagementFacade getProductManager() {
        return productManager;
    }

    public UserManagementFacade getUserManager() {
        return userManager;
    }

    public OrderAndCheckoutFacade getOrderer() {
        return orderer;
    }

    // This should also get data *from* the Shop entity
    public Long getId(Shop shop) { // The ID in Shop.java is Long
        return shop.getShopId();
    }
}