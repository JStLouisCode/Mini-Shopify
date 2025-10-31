package org.example;

import jakarta.persistence.*;

/// This current implementation isa stub and not the final implementation, it is only here to provide enough
/// functionality so the shop management features can work.
@Entity
public class UserManagementFacade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "owner_name")
    private String name;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private ShopManagementFacade shop;

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ShopManagementFacade getShop() {
        return shop;
    }

    public UserManagementFacade() {
        this.name = "Mr. Owner";
    }

    public void setShop(ShopManagementFacade shop) {
        this.shop = shop;
    }
    public int getId() { return id;}
    public String getName() {return name;}

}
