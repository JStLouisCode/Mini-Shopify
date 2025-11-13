package org.example.controller;

import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller class for handling shop-related web requests and operations.
 * Manages shop creation, editing, and display functionalities.
 */
@Controller
public class ShopController {

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Initializes the web data binder to disallow automatic binding of tags field.
     * @param binder the WebDataBinder to initialize
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // This stops Spring from trying to bind "tags" automatically
        // Prevents Spring from automatically populating the tags field during form submission
        binder.setDisallowedFields("tags");
    }

    /**
     * Displays the homepage with all shops.
     * @param model the model to add attributes to
     * @return the homepage view name
     */
    @GetMapping("/")
    public String index(Model model) {
        // Add all shops from repository to the model for display
        model.addAttribute("shops", shopRepository.findAll());
        return "homepage";
    }

    /**
     * Shows the form for creating a new shop.
     * @param model the model to add attributes to
     * @return the create-shop form view name
     */
    @GetMapping("/create-shop")
    public String createShopForm(Model model) {
        // Create empty shop object for form binding
        model.addAttribute("shop", new Shop());
        return "create-shop";
    }

    /**
     * Processes the creation of a new shop.
     * @param shop the shop entity to create
     * @param tagsString the comma-separated tags string
     * @return redirect to homepage
     */
    @PostMapping("/create-shop")
    public String createShop(@ModelAttribute Shop shop, @RequestParam("tags") String tagsString) {

        // 'shop' already has name, description, etc. bound automatically.
        // 'shop.tags' is an empty list because we disallowed binding.

        // Now, we process the tagsString manually:
        if (tagsString != null && !tagsString.isEmpty()) {
            // Process tags: split by comma, trim whitespace, and remove empty tags
            List<String> tagsList = Arrays.stream(tagsString.split(",")) // Split input string by commas into array
                    .map(String::trim)              // Remove leading/trailing whitespace from each tag
                    .filter(tag -> !tag.isEmpty())  // Filter out any empty strings
                    .collect(Collectors.toList());  // Convert stream back to List

            shop.setTags(tagsList); // Set the processed list on the entity
        }

        shopRepository.save(shop); // Save the completed object to database
        return "redirect:/"; // Redirect to homepage after successful creation
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
        // Load all shops for selection dropdown
        model.addAttribute("shops", shopRepository.findAll());
        return "select-shop";
    }

    /**
     * Shows the form for editing an existing shop.
     * @param id the ID of the shop to edit
     * @param model the model to add attributes to
     * @return the edit-shop form view name or redirect if shop not found
     */
    @GetMapping("/edit-shop/{id}")
    public String editShopForm(@PathVariable Long id, Model model) {
        // Find shop by ID - returns Optional to handle null case
        Optional<Shop> shop = shopRepository.findById(id);
        if (shop.isPresent()) {
            // Add found shop to model for form pre-population
            model.addAttribute("shop", shop.get());
            return "edit-shop";
        }
        // Redirect to selection page if shop doesn't exist
        return "redirect:/select-shop";
    }

    /**
     * Processes the update of an existing shop.
     * @param id the ID of the shop to update
     * @param shop the updated shop data
     * @param tagsString the comma-separated tags string
     * @return redirect to shop selection page
     */
    @PostMapping("/edit-shop/{id}")
    public String updateShop(@PathVariable Long id, @ModelAttribute Shop shop, @RequestParam("tags") String tagsString) {
        // Process tags if provided
        if (tagsString != null && !tagsString.isEmpty()) {
            // Convert comma-separated tags string to cleaned list
            List<String> tagsList = Arrays.stream(tagsString.split(","))
                    .map(String::trim)
                    .filter(tag -> !tag.isEmpty())
                    .collect(Collectors.toList());
            shop.setTags(tagsList);
        }
        // Ensure the ID from path variable is set on the shop object
        shop.setId(id);
        // Save updated shop to database
        shopRepository.save(shop);
        return "redirect:/select-shop"; // Redirect back to selection page
    }
}