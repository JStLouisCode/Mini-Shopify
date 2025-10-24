package org.example;

public class OrderAndCheckoutFacade {
    private ShopManagementFacade shop;
    private static OrderAndCheckoutFacade instance;

    public OrderAndCheckoutFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

    public void orderSomething (int productID){
        shop.getProductManager().getProducts();
            for (Product p : shop.getProductManager().getProducts()){
                if (p.getProductID() == productID) {
                    if (p.getQuantity() > 0){
                        p.setQuantityMinus1();
                    }
                    else {
                        System.out.println("There is no more of this product, error in ordering");
                    }
                }
                else {
                    System.out.println("Could not find product");
                }
            }
    }


}
