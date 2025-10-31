package org.example;

<<<<<<< HEAD
import jakarta.persistence.*;

import java.util.ArrayList;
@Entity
=======
import java.util.ArrayList;

>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
public class ProductManagementFacade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

<<<<<<< HEAD
    @OneToOne (fetch = FetchType.EAGER)
    @JoinColumn(name = "shop_id")
    private ShopManagementFacade shop;

    private ArrayList<Product> products = new ArrayList<>();

    public ProductManagementFacade() {}

=======
    private ShopManagementFacade shop;
    private ArrayList<Product> products;

>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
    public ProductManagementFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

<<<<<<< HEAD
    public void createProduct (String productName, String productDescription, double productPrice, String productCategory, int quantity) {
        products.add(new Product(productName,productDescription,productPrice,productCategory, quantity, this));
    }

    public void createProductViaObject (Product product) {
        products.add(product);
    }

=======
    public void createProduct (String productName, int productID, String productDescription, double productPrice, String productCategory, int quantity) {
        products.add(new Product(productName, productID,productDescription,productPrice,productCategory, quantity, shop));
    }

>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
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
<<<<<<< HEAD
        // Use an iterator or a temporary list to avoid ConcurrentModificationException
        products.removeIf(p -> p.getProductID() == productID);
=======
        for (Product p : products){
            if (p.getProductID() == productID) {
                products.remove(p);
            }
        }
>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
    }
    public void removeProductByObject(Product p) {products.remove(p);}

<<<<<<< HEAD
    public void removeProductByObject(Product p) {
        products.remove(p);
    }
}
=======
}
>>>>>>> parent of 2fdb141 (Merge remote-tracking branch 'origin/main' into jared)
