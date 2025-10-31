package org.example;

import java.util.ArrayList;

public class ShopManagementFacade {

    private String name;
    private ArrayList<String> tags;
    private String ShopType;
    public UserManagementFacade owner;
    private int id;
    public ProductManagementFacade products;
    public OrderAndCheckoutFacade orderer;

    public ShopManagementFacade(UserManagementFacade owner, int id) {
        this.owner = owner;
        this.id = id;
        products = new ProductManagementFacade(this);
        orderer = new OrderAndCheckoutFacade(this);
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


    public void setShopType(String shopType) {
        ShopType = shopType;
    }

    public ProductManagementFacade getProductManager (){
        return products;
    }

    //No setters for owner and ID for now for safety reasons
    public UserManagementFacade getOwner() {
        return owner;
    }

    public int getId() {return id;}


}
