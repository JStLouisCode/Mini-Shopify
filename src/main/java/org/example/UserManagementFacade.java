package org.example;

import jakarta.persistence.*;
import org.springframework.stereotype.Service;

/// This current implementation isa stub and not the final implementation, it is only here to provide enough
/// functionality so the shop management features can work.
@Service
public class UserManagementFacade {

    private int id;

    private String name;

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
