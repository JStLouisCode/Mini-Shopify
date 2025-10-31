package org.example.controller;

import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;


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

    @GetMapping("/shops")
    public String viewShops(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "view-shops";
    }

    @GetMapping("/select-shop")
    public String selectShop(Model model) {
        model.addAttribute("shops", shopRepository.findAll());
        return "select-shop";
    }

    @GetMapping("/edit-shop/{id}")
    public String editShopForm(@PathVariable Long id, Model model) {
        Optional<Shop> shop = shopRepository.findById(id);
        if (shop.isPresent()) {
            model.addAttribute("shop", shop.get());
            return "edit-shop";
        }
        return "redirect:/select-shop";
    }

    @PostMapping("/edit-shop/{id}")
    public String updateShop(@PathVariable Long id, @ModelAttribute Shop shop) {
        shop.setId(id);
        shopRepository.save(shop);
        return "redirect:/select-shop";
    }

}
