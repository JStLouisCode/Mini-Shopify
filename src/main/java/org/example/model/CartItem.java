package org.example.model;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Cart cart;

    @ManyToOne
    private Product product;

    private int quantity;

    public CartItem() {}

    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
    public Cart getCart() {
        return cart;
    }
    public Product getProduct() {
        return product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "CartItem{" + "id=" + id + ", cart=" + cart + ", product=" + product + ", quantity=" + quantity + '}';
    }

}

