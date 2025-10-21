package org.example;

public class OrderAndCheckoutFacade {
    private ShopManagementFacade shop;
    private static OrderAndCheckoutFacade instance;

    public OrderAndCheckoutFacade(ShopManagementFacade shop) {
        this.shop = shop;
    }

    public void orderSomething (int productID){
        shop.getProducts();
            for (ProductManagementFacade p : shop.getProducts()){

                if (p.getQuantity() > 0){
                    if (p.getProductID() == productID) {
                        p.setQuantityMinus1();
                    }
                    else {
                        System.out.println("There is no more of this product, error in ordering");
                    }
                }

            }
    }


}
