package org.example;

import org.example.model.Product;
import org.example.model.Shop; // Import the Shop entity
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List; // Import List

@Service
public class ProductManagementFacade {

    // A service is (ideally) stateless.
    // We remove the 'id', 'shop', and 'products' fields.
    // The Shop entity will hold its own list of products.

    public ProductManagementFacade() {}

    /**
     * Creates a new Product and adds it to the given Shop's product list.
     * @param shop The Shop entity to add the new product to.
     */
    public void createProduct (Shop shop, String productName, String productDescription, double productPrice, String productCategory, int quantity) {

        // 1. Create the new Product.
        // We pass the 'shop' entity, which is the correct type.
        Product newProduct = new Product(productName, productDescription, productPrice, productCategory, quantity, shop);

        // 2. Add the new Product to the Shop's actual list.
        // This is the list that is mapped to the database.
        shop.getProducts().add(newProduct);
    }

    /**
     * Gets the list of products from a specific Shop.
     */
    public List<Product> getProducts(Shop shop) {
        // We return the list directly from the Shop entity
        return shop.getProducts();
    }

    /**
     * Finds a product by its ID from a specific Shop's product list.
     */
    public Product findProductByID(Shop shop, int productID) {
        // We search the list from the Shop entity
        for (Product p : shop.getProducts()){
            if (p.getProductID() == productID){
                return p;
            }
        }
        System.out.println("Could not find product");
        return null;
    }

    /**
     * Removes a product by its ID from a specific Shop's product list.
     */
    public void removeProductByID(Shop shop, int productID) {
        // We remove from the list in the Shop entity
        // The 'orphanRemoval = true' in Shop.java will handle deleting it from the DB.
        shop.getProducts().removeIf(p -> p.getProductID() == productID);
    }

    /**
     * Removes a specific product object from a Shop's product list.
     */
    public void removeProductByObject(Shop shop, Product p) {
        // We remove from the list in the Shop entity
        shop.getProducts().remove(p);
    }
}