package org.example;

import org.example.model.Product;
import org.example.model.Shop; // Import the Shop entity
import org.springframework.beans.factory.annotation.Autowired; // Import Autowired
import org.springframework.stereotype.Service;

@Service
public class OrderAndCheckoutFacade {

    // 1. Autowire the ProductManagementFacade so this service can use it.
    // Spring will automatically inject the one-and-only instance.
    @Autowired
    private ProductManagementFacade productManager;

    // 2. Remove the 'shop' field and constructor. This service is now stateless.
    public OrderAndCheckoutFacade() {}

    /**
     * "Orders" a product by decreasing its quantity.
     * @param shop The Shop entity to order from.
     * @param productID The ID of the product to order.
     */
    public void orderSomething (Shop shop, int productID){

        // 3. Use the productManager to find the product *first*.
        // This is much cleaner than looping!
        Product p = productManager.findProductByID(shop, productID);

        // 4. Check if the product was found
        if (p != null) {
            // Product was found, now check quantity
            if (p.getQuantity() > 0){
                p.setQuantityMinus1();
                System.out.println("Order successful for product: " + p.getProductID());
            }
            else {
                System.out.println("There is no more of this product, error in ordering");
            }
        } else {
            // This 'else' correctly triggers only if the product was never found
            System.out.println("Could not find product with ID: " + productID);
        }
    }
}