package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
@Entity
public class ProductManagementFacade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private ShopManagementFacade shop;

    private ArrayList<Product> products = new ArrayList<>();

    public ProductManagementFacade() {}

    public ProductManagementFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

    public void createProduct (String productName, String productDescription, double productPrice, String productCategory, int quantity) {
        products.add(new Product(productName,productDescription,productPrice,productCategory, quantity, this));
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