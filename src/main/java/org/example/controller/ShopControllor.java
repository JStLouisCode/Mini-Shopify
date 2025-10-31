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
import java.util.stream.Collectors;

@Controller
public class ShopControllor {

    @Autowired
    private ShopRepository shopRepository;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // This stops Spring from trying to bind "tags" automatically
        binder.setDisallowedFields("tags");
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "homepage";
    }

    @GetMapping("/create-shop")
    public String createShopForm(Model model) {
        model.addAttribute("shop", new Shop());
        return "create-shop";
    }

    @PostMapping("/create-shop")
    public String createShop(@ModelAttribute Shop shop, @RequestParam("tags") String tagsString) {

        // 'shop' already has name, description, etc. bound automatically.
        // 'shop.tags' is an empty list because we disallowed binding.

        // Now, we process the tagsString manually:
        if (tagsString != null && !tagsString.isEmpty()) {
            List<String> tagsList = Arrays.stream(tagsString.split(",")) // Split by comma
                    .map(String::trim)              // Trim whitespace
                    .filter(tag -> !tag.isEmpty())  // Remove any empty strings
                    .collect(Collectors.toList());

            shop.setTags(tagsList); // Set the processed list on the entity
        }

        shopRepository.save(shop); // Save the completed object
        return "redirect:/";
    }

    @GetMapping("/products")
    public String products() {
        return "products";
    }

    @GetMapping("/orders")
    public String orders() {
        return "orders";
    }

    @GetMapping("/view-existing-shops")
    public String ViewExistingShops(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "view-existing-shops";
    }
}