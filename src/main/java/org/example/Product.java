package org.example;
/// This is a stub only here to support ShopManagementFacade and has not been fully implemented yet.
public class Product {
    private String productName;
    private String productDescription;
    private double productPrice;
    private String productCategory;
    private int productID;
    private int quantity;
    private ShopManagementFacade shop;

    public Product(String productName, int productID, String productDescription, double productPrice, String productCategory, int quantity, ShopManagementFacade shop) {
        this.productName = productName;
        this.productID = productID;
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

    public ShopManagementFacade getShop() {
        return shop;
    }

    public void setQuantityMinus1() {

        this.quantity = quantity - 1;
    }
}
