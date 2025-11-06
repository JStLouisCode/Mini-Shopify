package org.example.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import org.example.model.Product;

/**
 * Represents a shop entity in the Mini-Shopify system.
 * Contains shop information, tags, and associated products.
 * This class is mapped to a database table and manages shop-related data.
 */
@Entity
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; // Primary key, automatically generated

    private String name; // Name of the shop

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "shop_tags", joinColumns = @JoinColumn(name = "shop_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>(); // Tags associated with the shop, stored in separate table

    private String description; // Description of the shop
    private String businessType; // Type of business (e.g., retail, service, etc.)
    private String currency; // Currency used by the shop
    private String contact; // Contact information for the shop
    private String socialMediaLinks; // Social media links for the shop

    @OneToMany(mappedBy = "shop", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>(); // One-to-many relationship with Product entities

    // GETTER METHODS

    /**
     * Gets the shop ID.
     * @return the shop ID
     */
    public Long getId() {
        return id;
    }

    /**
     * Gets the shop name.
     * @return the shop name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of tags associated with the shop.
     * @return list of tags
     */
    public List<String> getTags() { // Returns List<String>
        return tags;
    }

    /**
     * Gets the list of products associated with the shop.
     * @return list of products
     */
    public List<Product> getProducts() {
        return this.products;
    }

    /**
     * Gets the shop description.
     * @return the shop description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the business type.
     * @return the business type
     */
    public String getBusinessType() {
        return businessType;
    }

    /**
     * Gets the currency used by the shop.
     * @return the currency
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Gets the contact information.
     * @return the contact information
     */
    public String getContact() {
        return contact;
    }

    /**
     * Gets the social media links.
     * @return the social media links
     */
    public String getSocialMediaLinks() {
        return socialMediaLinks;
    }

    // SETTER METHODS

    /**
     * Sets the shop ID.
     * @param id the shop ID to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Sets the shop name.
     * @param name the shop name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the list of tags for the shop.
     * @param tags the list of tags to set
     */
    public void setTags(List<String> tags) { // Accepts List<String>
        this.tags = tags;
    }

    /**
     * Sets the shop description.
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the business type.
     * @param businessType the business type to set
     */
    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    /**
     * Sets the currency used by the shop.
     * @param currency the currency to set
     */
    public void setCurrency(String currency) {
        this.currency = currency;
    }

    /**
     * Sets the contact information.
     * @param contact the contact information to set
     */
    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * Sets the social media links.
     * @param socialMediaLinks the social media links to set
     */
    public void setSocialMediaLinks(String socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }
}