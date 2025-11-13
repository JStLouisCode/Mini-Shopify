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

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

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