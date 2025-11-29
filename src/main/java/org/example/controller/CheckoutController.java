package org.example.controller;

import org.example.model.*;
import org.example.repository.OrderRepository;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@SessionAttributes("cart")
public class CheckoutController {

    private final OrderRepository orderRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;

    public CheckoutController(OrderRepository orderRepository,
                              ShopRepository shopRepository,
                              ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.shopRepository = shopRepository;
        this.productRepository = productRepository;
    }

    public static class CheckoutForm {
        @NotBlank
        private String name;
        @NotBlank @Email
        private String email;
        @NotBlank
        private String address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    @GetMapping("/cart/checkout")
    public String showCheckout(@ModelAttribute("cart") Cart cart,
                               @RequestParam(required = false) Long shopId,
                               Model model) {

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            return "redirect:/cart";
        }

        Long effectiveShopId = (shopId != null) ? shopId : cart.getOwnerShopId();
        Shop shop = (effectiveShopId != null)
                ? shopRepository.findById(effectiveShopId).orElse(null)
                : null;

        model.addAttribute("cart", cart);
        model.addAttribute("shop", shop);
        model.addAttribute("currency", cart.getCurrency());
        model.addAttribute("checkoutForm", new CheckoutForm());

        return "checkout";
    }


    @PostMapping("/cart/checkout")
    public String submitCheckout(@ModelAttribute("cart") Cart cart,
                                 @ModelAttribute("checkoutForm") CheckoutForm form,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {

        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Your cart is empty.");
            return "redirect:/cart";
        }

        // Check stock for each item
        for (CartItem cartItem : cart.getItems()) {
            Long productId = cartItem.getProduct().getProductID();   // adjust getter if needed
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + productId));

            int requested = cartItem.getQuantity();
            int available = product.getProductInventory();           // or getInventory()

            if (available < requested) {
                redirectAttributes.addFlashAttribute(
                        "errorMessage",
                        "Not enough stock for '" + product.getProductName()
                                + "'. Available: " + available + ", requested: " + requested
                );
                return "redirect:/cart/checkout";
            }
        }

        // Build the Order
        Long shopId = cart.getOwnerShopId(); // or getShopId()
        Shop shop = (shopId != null)
                ? shopRepository.findById(shopId).orElse(null)
                : null;

        Order order = new Order();
        order.setShopId(shopId);
        if (shop != null) {
            order.setShopName(shop.getName());
            order.setCurrency(shop.getCurrency());
        } else {
            order.setCurrency(cart.getCurrency());
        }

        order.setCustomerName(form.getName());
        order.setCustomerEmail(form.getEmail());
        order.setShippingAddress(form.getAddress());
        order.setTotalPrice(cart.getTotalPrice());

        for (CartItem cartItem : cart.getItems()) {
            OrderItem oi = new OrderItem();
            oi.setProductName(cartItem.getProduct().getProductName());
            oi.setUnitPrice(cartItem.getProduct().getProductPrice());
            oi.setQuantity(cartItem.getQuantity());
            oi.setLineTotal(cartItem.getProduct().getProductPrice() * cartItem.getQuantity());
            order.addItem(oi);
        }

        // Persist order
        Order saved = orderRepository.save(order);

        // After saving order, actually deduct stock
        for (CartItem cartItem : cart.getItems()) {
            Long productId = cartItem.getProduct().getProductID();
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product id: " + productId));

            int newInventory = product.getProductInventory() - cartItem.getQuantity();
            product.setProductInventory(newInventory);
            productRepository.save(product);
        }

        // Clear cart
        cart.clear();

        return "redirect:/order/confirmation/" + saved.getId();
    }

    @GetMapping("/order/confirmation/{orderId}")
    public String showOrderConfirmation(@PathVariable Long orderId,
                                        Model model) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order id"));

        model.addAttribute("order", order);
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        model.addAttribute("orders", orders);
        return "orders"; // orders.html
    }
}

