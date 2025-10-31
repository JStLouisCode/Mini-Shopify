package org.example.model;

import jakarta.persistence.*;
import org.example.ProductManagementFacade;

/// This is a stub only here to support ShopManagementFacade and has not been fully implemented yet.
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productID;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "product_desc")
    private String productDescription;
    @Column(name = "product_cost")
    private double productPrice;
    @Column(name = "product_category")
    private String productCategory;
    @Column(name = "product_quantity")
    private int quantity;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "facade_id")
    private Shop shop;

    public Product() {}

    public Product(String productName, String productDescription, double productPrice, String productCategory, int quantity, Shop shop) {

        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productCategory = productCategory;
        this.quantity = quantity;
        this.shop = shop;
    }

    /// For safety and simplicity there are only setters for desc. and price. Do not worry about others right now
    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public int getProductID() {
        return productID;
    }
    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    public Shop getShop() {
        return shop;
    }

    public void setQuantityMinus1() {

        this.quantity = quantity - 1;
    }
}
