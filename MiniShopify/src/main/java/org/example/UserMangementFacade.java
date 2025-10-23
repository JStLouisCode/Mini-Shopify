package org.example;

/// This current implementation isa stub and not the final implementation, it is only here to provide enough
/// functionality so the shop management features can work.
public class UserMangementFacade {
    private int id;
    private String name;
    private ShopManagementFacade shop;

    public  UserMangementFacade() {
        this.id = 1;
        this.name = "Mr. Owner";
        this.shop = new ShopManagementFacade(this,0);
    }
    public int getId() { return id;}
    public String getName() {return name;}

}
