package org.example;

<<<<<<< HEAD
import jakarta.persistence.*;

import java.util.ArrayList;
@Entity
=======
import java.util.ArrayList;

>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
public class ShopManagementFacade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

<<<<<<< HEAD
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
=======
    private String name;
    private ArrayList<String> tags;
    private String ShopType;
    private UserMangementFacade owner;
    private int id;
    private ProductManagementFacade products;
    private OrderAndCheckoutFacade orderer;

    public ShopManagementFacade(UserMangementFacade owner, int id) {
        this.owner = owner;
        this.id = id;
>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
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
<<<<<<< HEAD
    public UserManagementFacade getOwner() {
=======
    public UserMangementFacade getOwner() {
>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
        return owner;
    }

    public int getId() {return id;}


}
