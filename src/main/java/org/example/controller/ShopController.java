package org.example.controller;

import jakarta.validation.Valid;
import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Make sure this is imported
import org.example.model.Product;
import org.example.repository.ProductRepository;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Controller
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private ProductRepository productRepository;

    private final List<String> PREDEFINED_TAGS = List.of(
            "Electronics", "Books", "Clothing", "Home", "Toys", "Sports", "Grocery"
    );

    private final Map<String, String> PREDEFINED_CURRENCIES = Map.of(
            "USD", "USD - US Dollar",
            "CAD", "CAD - Canadian Dollar",
            "EUR", "EUR - Euro",
            "GBP", "GBP - British Pound",
            "JPY", "JPY - Japanese Yen"
    );

    private final List<String> PREDEFINED_BUSINESS_TYPES = List.of(
            "Retail",
            "Services",
            "Food & Beverage",
            "Technology",
            "Healthcare",
            "Manufacturing",
            "Government",
            "Education",
            "Other"
    );

    // --- ADDED BACK ---
    // You need this for your homepage to load
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "homepage"; // Assuming homepage.html is your main view
    }
    // -----------------

    @GetMapping("/create-shop")
    public String createShopForm(Model model) {
        model.addAttribute("shop", new Shop());
        model.addAttribute("allTags", PREDEFINED_TAGS);
        model.addAttribute("allCurrencies", PREDEFINED_CURRENCIES);
        model.addAttribute("allBusinessTypes", PREDEFINED_BUSINESS_TYPES);
        return "create-shop";
    }

    @PostMapping("/create-shop")
    public String createShop(@ModelAttribute Shop shop) {
        shopRepository.save(shop);
        return "redirect:/";
    }

    /**
     * Displays the products page.
     * @return the products view name
     */
    @GetMapping("/shop/{id}")
    public String products(@PathVariable long id, Model model) {
        Optional<Shop> shop = shopRepository.findById(id);

        if (shop.isPresent()){
            model.addAttribute("shop", shop.get());
            return "products";
        }

        return "Error";
    }




    /**
     * Displays the orders page.
     * @return the orders view name
     */
    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }

    /**
     * Displays all existing shops.
     * @param model the model to add attributes to
     * @return the view-existing-shops view name
     */
    @GetMapping("/view-existing-shops")
    public String ViewExistingShops(Model model) {
        // Retrieve all shops from database and add to model
        model.addAttribute("shops", shopRepository.findAll());
        return "view-existing-shops";
    }

    /**
     * Shows the shop selection page.
     * @param model the model to add attributes to
     * @return the select-shop view name
     */
    @GetMapping("/select-shop")
    public String selectShop(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "select-shop";
    }
    // -----------------

    @GetMapping("/edit-shop/{id}")
    // === FIX WAS HERE ===
    // You were missing @PathVariable("id") before Long id
    public String editShopForm(@PathVariable("id") Long id, Model model) {
        Optional<Shop> shop = shopRepository.findById(id);
        if (shop.isPresent()) {
            model.addAttribute("shop", shop.get());
            model.addAttribute("allTags", PREDEFINED_TAGS);
            model.addAttribute("allCurrencies", PREDEFINED_CURRENCIES);
            model.addAttribute("allBusinessTypes", PREDEFINED_BUSINESS_TYPES);
            return "edit-shop";
        }
        return "redirect:/select-shop";
    }

    @PostMapping("/edit-shop/{id}")
    // This method also needs @PathVariable("id")
    public String updateShop(@PathVariable("id") Long id, @ModelAttribute Shop shop) {
        shop.setId(id); // Set the ID from the path
        shopRepository.save(shop);
        return "redirect:/select-shop";
    }

    // ========== REST API ENDPOINTS (for tests) ==========

    /**
     * REST API endpoint to create a shop.
     * Returns JSON with HTTP status codes.
     */
    @PostMapping("/shops")
    @ResponseBody
    public ResponseEntity<Shop> createShopApi(@Valid @RequestBody Shop shop) {
        Shop savedShop = shopRepository.save(shop);
        URI location = URI.create("/shops/" + savedShop.getShopId());
        return ResponseEntity.created(location).body(savedShop);
    }

    /**
     * REST API endpoint to get all shops.
     */
    @GetMapping("/shops")
    @ResponseBody
    public ResponseEntity<List<Shop>> getAllShopsApi() {
        List<Shop> shops = shopRepository.findAll();
        return ResponseEntity.ok(shops);
    }

    /**
     * REST API endpoint to get a shop by ID.
     */
    @GetMapping("/shops/{id}")
    @ResponseBody
    public ResponseEntity<Shop> getShopByIdApi(@PathVariable Long id) {
        Optional<Shop> shop = shopRepository.findById(id);
        return shop.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Displays the product management page for a specific shop.
     * This page shows existing products and a form to add new ones.
     *
     * @param shopId The ID of the shop to manage.
     * @param model  The Spring UI model.
     * @return The 'manage-products' view.
     */
    @GetMapping("/shop/{shopId}/manage")
    public String manageProducts(@PathVariable("shopId") Long shopId, Model model) {
        Optional<Shop> shopOpt = shopRepository.findById(shopId);

        if (shopOpt.isPresent()) {
            Shop shop = shopOpt.get();
            model.addAttribute("shop", shop);
            // Add an empty Product object to bind to the "Add Product" form
            model.addAttribute("newProduct", new Product());
            model.addAttribute("allTags", PREDEFINED_TAGS);
            return "manage-products";
        }

        // If shop not found, redirect to a safe page
        return "redirect:/select-shop";
    }

    /**
     * Handles the form submission for adding a new product to a shop.
     *
     * @param shopId  The ID of the shop to add the product to.
     * @param product The Product object from the form (@ModelAttribute).
     * @return A redirect back to the management page.
     */
    @PostMapping("/shop/{shopId}/products/add")
    public String addProduct(@PathVariable("shopId") Long shopId, @ModelAttribute Product product) {
        Optional<Shop> shopOpt = shopRepository.findById(shopId);

        if (shopOpt.isPresent()) {
            Shop shop = shopOpt.get();
            // Set the parent shop on the new product
            product.setShop(shop);
            // Save the new product
            productRepository.save(product);
        }

        // Redirect back to the management page for the same shop
        return "redirect:/shop/" + shopId + "/manage";
    }

    @GetMapping("/shop/{shopId}/products/{productId}/edit")
    public String editProduct(@PathVariable("shopId") Long shopId, @PathVariable("productId") Long productId, Model model) {
        Optional<Shop> shopOpt = shopRepository.findById(shopId);
        Optional<Product> productOpt = productRepository.findById(productId);
        if (shopOpt.isPresent() && productOpt.isPresent()) {
            Shop shop = shopOpt.get();
            model.addAttribute("shop", shop);
            model.addAttribute("product", productOpt.get());
            model.addAttribute("allTags", PREDEFINED_TAGS);
            return "edit-product";
        }
        return "redirect:/shop/" + shopId + "/manage";
    }

    @PostMapping("/shop/{shopId}/products/{productId}/edit")
    public String updateProduct(@PathVariable("shopId") Long shopId, @PathVariable("productId") Long productId, @ModelAttribute Product product) {
        Optional<Shop> shopOpt = shopRepository.findById(shopId);
        Optional<Product> productOpt = productRepository.findById(productId);

        if (shopOpt.isPresent() && productOpt.isPresent()) {
            Product existingProduct = productOpt.get();

            // Update fields from form
            existingProduct.setProductName(product.getProductName());
            existingProduct.setProductDescription(product.getProductDescription());
            existingProduct.setProductPrice(product.getProductPrice());
            existingProduct.setProductCategory(product.getProductCategory());
            existingProduct.setProductInventory(product.getProductInventory());
            existingProduct.setPictureUrl(product.getPictureUrl());

            // Save updated product
            productRepository.save(existingProduct);

            return "redirect:/shop/" + shopId + "/manage";
        }

        return "redirect:/select-shop";
    }

    /**
     * Handles the form submission for deleting a product from a shop.
     * This method is called when the user clicks the "Delete" button.
     *
     * @param productId The ID of the product to delete.
     * @return A redirect back to the management page for the same shop.
     */
    @PostMapping("/products/{id}/delete")
    public String deleteProduct(@PathVariable("id") Long productId) {
        Long shopId = null;

        // Find the product so we know which shop to redirect back to
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();

            // Get the parent shop (may be null if not set for some reason)
            Shop shop = product.getShop();
            if (shop != null) {
                shopId = shop.getShopId();   // adjust getter name if needed
            }

            // Delete the product
            productRepository.delete(product);
        }

        // Redirect back to the manage page for that shop if we know it,
        // otherwise fall back to the shop selection page.
        if (shopId != null) {
            return "redirect:/shop/" + shopId + "/manage";
        } else {
            return "redirect:/select-shop";
        }
    }


    /**
     * REST API endpoint to update a shop.
     */
    @PutMapping("/shops/{id}")
    @ResponseBody
    public ResponseEntity<Void> updateShopApi(@PathVariable Long id, @Valid @RequestBody Shop shop) {
        if (!shopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        shop.setId(id);
        shopRepository.save(shop);
        return ResponseEntity.noContent().build();
    }

    /**
     * REST API endpoint to delete a shop.
     */
    @DeleteMapping("/shops/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteShopApi(@PathVariable Long id) {
        if (!shopRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        shopRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}