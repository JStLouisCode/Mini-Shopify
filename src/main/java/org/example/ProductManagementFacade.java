package org.example;

import java.util.ArrayList;

public class ProductManagementFacade {

    public ShopManagementFacade shop;
    // Initialize the ArrayList here
    public ArrayList<Product> products = new ArrayList<>();

    public ProductManagementFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

    public void createProduct (String productName, int productID, String productDescription, double productPrice, String productCategory, int quantity) {
        // This line will now work correctly
        products.add(new Product(productName, productID,productDescription,productPrice,productCategory, quantity, shop));
    }

    public ArrayList<Product> getProducts() {
        return products;
    }

    public Product findProductByID(int productID) {
        for (Product p : products){
            if (p.getProductID() == productID){
                return p;
            }
        }
        System.out.println("Could not find product");
        return null;
    }

    public void removeProductByID(int productID) {
        // Use an iterator or a temporary list to avoid ConcurrentModificationException
        products.removeIf(p -> p.getProductID() == productID);
    }

    public void removeProductByObject(Product p) {
        products.remove(p);
    }
}