package org.example.controller;

import org.example.model.Cart;
import org.example.model.CartItem;
import org.example.model.Product;
import org.example.model.Shop;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
@SessionAttributes("cart")
public class CartController {

    private final ProductRepository productRepository;
    private final ShopRepository shopRepository;

    public CartController(ProductRepository productRepository, ShopRepository shopRepository) {
        this.productRepository = productRepository;
        this.shopRepository = shopRepository;
    }

    @ModelAttribute("cart")
    public Cart cart() {
        return new Cart();
    }

    @GetMapping
    public String viewCart(@RequestParam(required = false) Long shopId, @ModelAttribute("cart") Cart cart, Model model) {
        model.addAttribute("cart", cart);
        model.addAttribute("shopId", shopId != null ? shopId : cart.getOwnerShopId());
        model.addAttribute("currency", cart.getCurrency());
        return "cart";
    }

    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam Long shopId, @RequestParam(value = "confirmClear", required = false) String confirmClear,
                            @ModelAttribute("cart") Cart cart, Model model) {

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product id"));
        Shop newShop = shopRepository.findById(shopId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid shop id"));

        // Just for debugging if needed:
        System.out.printf("Cart shopId=%s, items=%d, new shopId=%d, confirmClear=%s%n", cart.getOwnerShopId(), cart.getItems().size(), shopId, confirmClear);

        boolean cartHasItems = !cart.getItems().isEmpty();
        boolean cartHasShop  = cart.getOwnerShopId() != null;
        boolean differentShop = cartHasShop && !cart.getOwnerShopId().equals(shopId);

        // 1️⃣ Different shop, user has NOT confirmed yet -> show confirmation page
        if (cartHasItems && differentShop && (confirmClear == null || !confirmClear.equalsIgnoreCase("yes"))) {

            model.addAttribute("cart", cart);
            model.addAttribute("currentShopId", cart.getOwnerShopId());
            model.addAttribute("newShop", newShop);
            model.addAttribute("product", product);
            model.addAttribute("newShopId", shopId);

            return "confirm-clear-cart";
        }

        // 2️⃣ Different shop, user said "yes" -> clear first
        if (cartHasItems && differentShop && "yes".equalsIgnoreCase(confirmClear)) {
            cart.clear();
        }

        // 3️⃣ Now safe to add the product (first item OR same shop OR just cleared)
        cart.addProduct(product, newShop, 1);

        return "redirect:/cart?shopId=" + cart.getOwnerShopId();
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam Long productId,
                                 @ModelAttribute("cart") Cart cart) {
        cart.removeProduct(productId);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@RequestParam Long productId,
                                 @RequestParam int quantity,
                                 @ModelAttribute("cart") Cart cart) {
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getProductID()==(productId)) {
                item.setQuantity(quantity);
                break;
            }
        }
        return "redirect:/cart";
    }

    /**
     * Handles IllegalArgumentException thrown by controller methods.
     * This is typically thrown when invalid product IDs or shop IDs are provided
     * in cart operations.
     *
     * @param e the IllegalArgumentException containing details about the invalid argument
     * @return ResponseEntity with HTTP 400 Bad Request status and the error message in the body
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
