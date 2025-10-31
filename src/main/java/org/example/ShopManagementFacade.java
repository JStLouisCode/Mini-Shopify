package org.example;

import jakarta.persistence.*;

import java.util.ArrayList;
@Entity
public class ShopManagementFacade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "shop_name")
    private String name;

    @ElementCollection
    @CollectionTable(name = "shop_tags", joinColumns = @JoinColumn(name = "shop_id"))
    @Column(name = "tag")

    private ArrayList<String> tags = new ArrayList<>();
    @Column(name = "shop_shopType")
    private String ShopType;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id")
    private UserManagementFacade owner;
    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "product_manager_id")
    private ProductManagementFacade products;
    @Transient
    private OrderAndCheckoutFacade orderer;

    public ShopManagementFacade(UserManagementFacade owner) {
        this.owner = owner;
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
