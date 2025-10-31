package org.example.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import org.example.model.Product;

@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shop_tags", joinColumns = @JoinColumn(name = "shop_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();
    private String description;
    private String businessType;
    private String currency;
    private String contact;
    private String socialMediaLinks;

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();



    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() { // Returns List<String>
        return tags;
    }

    public void setTags(List<String> tags) { // Accepts List<String>
        this.tags = tags;
    }

    public List<Product> getProducts() {
        return this.products;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(String socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }
}