package org.example;

/**
import org.example.model.Product;
import org.example.model.Shop;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ModelTest {

    private Product product;
    private Shop shop;
    private ProductManagementFacade productFacade;
    private ShopManagementFacade shopFacade;
    private UserManagementFacade userFacade;

    @BeforeEach
    void setUp() {
        userFacade = new UserManagementFacade();
        shopFacade = new ShopManagementFacade(userFacade);
        productFacade = new ProductManagementFacade(shopFacade);

        product = new Product(
                "Laptop",
                "High-end gaming laptop",
                1999.99,
                "Electronics",
                10,
                productFacade
        );

        shop = new Shop();
        shop.setName("Tech Store");
    }

    // Product
    @Test
    void testProductInitialization() {
        assertEquals("Laptop", product.getProductName());
        assertEquals(1999.99, product.getProductPrice());
    }

    @Test
    void testSettersAndGetters() {
        product.setProductName("Phone");
        product.setProductPrice(899.99);
        assertEquals("Phone", product.getProductName());
        assertEquals(899.99, product.getProductPrice());
    }

    // Shop
    @Test
    void testShopHasName() {
        assertEquals("Tech Store", shop.getName());
    }

    // ProductManagementFacade
    @Test
    void testRemoveProductViaFacade() {
        productFacade.createProductViaObject(product);
    }


    // UserManagementFacade
    @Test
    void testUserFacadeInitialization() {
        assertNotNull(userFacade);
    }
}
 **/
