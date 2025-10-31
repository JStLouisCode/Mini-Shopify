package org.example;

import java.util.ArrayList;

public class ProductManagementFacade {

    private ShopManagementFacade shop;
    private ArrayList<Product> products;

    public ProductManagementFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

    public void createProduct (String productName, int productID, String productDescription, double productPrice, String productCategory, int quantity) {
        products.add(new Product(productName, productID,productDescription,productPrice,productCategory, quantity, shop));
    }

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
        for (Product p : products){
            if (p.getProductID() == productID) {
                products.remove(p);
            }
        }
    }
    public void removeProductByObject(Product p) {products.remove(p);}

}
