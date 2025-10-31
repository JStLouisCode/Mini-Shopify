package org.example.controller;

import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ShopControllor {

    @Autowired
    private ShopRepository shopRepository;

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
    public String createShop(@ModelAttribute Shop shop) {
        shopRepository.save(shop);
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
}