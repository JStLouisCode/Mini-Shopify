package org.example.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerShopId;
    private String currency;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();

    public void addProduct(Product product, Shop shop, int quantity) {
        if (ownerShopId == null) {
            this.ownerShopId = shop.getShopId();
            this.currency = shop.getCurrency();
        }

        // merge with existing item if present
        for (CartItem item : items) {
            if (item.getProduct().getProductID()==(product.getProductID())) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }
        CartItem newItem = new CartItem(this, product, quantity);
        items.add(newItem);
    }

    public void removeProduct(Long productId) {
        items.removeIf(item -> item.getProduct().getProductID()==(productId));
    }

    public int getTotalQuantity() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public double getTotalPrice() {
        return items.stream()
                .mapToDouble(i -> i.getProduct().getProductPrice() * i.getQuantity())
                .sum();
    }

    public Long getId() {
        return id;
    }
    public List<CartItem> getItems() {
        return items;
    }
    public void setItems(List<CartItem> items) {
        this.items = items;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getOwnerShopId() {
        return ownerShopId;
    }
    public void setOwnerShopId(Long shopId) {
        this.ownerShopId = shopId;
    }
    public String getCurrency() {
        return currency;
    }
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void clear() {
        items.clear();
        ownerShopId = null;
        currency = null;
    }
    public boolean isEmpty() {
        return items.isEmpty();
    }
}

