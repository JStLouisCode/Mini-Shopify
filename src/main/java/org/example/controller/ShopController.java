package org.example.controller;

import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*; // Make sure this is imported

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
    @GetMapping("/products")
    public String products() {
        return "products";
    }

    /**
     * Displays the orders page.
     * @return the orders view name
     */
    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }

    @GetMapping("/shop/{id}")
    public String products(@PathVariable long id, Model model) {
        Optional<Shop> shop = shopRepository.findById(id);

        if (shop.isPresent()){
            model.addAttribute("shop", shop.get());
            return "shop";
        }

        return "Error";
    }

    @GetMapping("/select-shop")
    public String selectShop(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "select-shop";
    }


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

    @GetMapping("/view-existing-shops")
    public String viewExistingShops(Model model) {
        // This line gets all shops from the database
        model.addAttribute("shops", shopRepository.findAll());

        // This tells Spring to use your "view-existing-shops.html" file
        return "view-existing-shops";
    }
}