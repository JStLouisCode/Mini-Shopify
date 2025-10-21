package org.example;

import java.util.ArrayList;

public class ShopManagementFacade {

    private String name;
    private ArrayList<String> tags;
    private ArrayList<ProductManagementFacade> products;
    private String ShopType;
    private UserMangementFacade owner;
    private int id;

    public ShopManagementFacade(UserMangementFacade owner, int id) {
        this.owner = owner;
        this.id = id;




    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public String getShopType() {
        return ShopType;
    }

    public void createProduct (String productName, int productID, String productDescription, double productPrice, String productCategory, int quantity) {
        products.add(new ProductManagementFacade(productName, productID,productDescription,productPrice,productCategory, quantity, this));
    }

    public ArrayList<ProductManagementFacade> getProducts() {
        return products;
    }

    public ProductManagementFacade findProductByID(int productID) {
        for (ProductManagementFacade p : products){
            if (p.getProductID() == productID){
                return p;
            }
        }
        System.out.println("Could not find product");
        return null;
    }

    public void removeProductByID(int productID) {
        for (ProductManagementFacade p : products){
            if (p.getProductID() == productID) {
                products.remove(p);
            }
    }
    }
    public void removeProductByObject(ProductManagementFacade p) {products.remove(p);}

    public void setShopType(String shopType) {
        ShopType = shopType;
    }

    //No setters for owner and ID for now for safety reasons
    public UserMangementFacade getOwner() {
        return owner;
    }

    public int getId() {return id;}


}
