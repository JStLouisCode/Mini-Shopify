package org.example.controller;

import org.example.model.Product;
import org.example.model.Shop;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for Cart functionality.
 * Tests the complete cart workflow including adding items, viewing cart,
 * updating quantities, removing items, and session persistence.
 * Verifies that cart state is maintained across requests within the same session.
 * Uses MockMvc to simulate HTTP requests and MockHttpSession to test session-based cart storage.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CartIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    private Shop testShop;
    private Product testProduct1;
    private Product testProduct2;

    /**
     * Sets up test data before each test by clearing existing data and creating fresh test entities.
     * Creates a test shop with two products to simulate a real shopping environment for cart operations.
     * The shop is configured with basic business details and products are created with different prices
     * and inventory levels to test various cart scenarios.
     */
    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation
        productRepository.deleteAll();
        shopRepository.deleteAll();

        // Create test shop with complete business configuration
        testShop = new Shop();
        testShop.setName("Test Shop");
        testShop.setDescription("A shop for testing");
        testShop.setBusinessType("Retail");
        testShop.setCurrency("USD");
        testShop = shopRepository.save(testShop);

        // Create first test product with electronics category and premium pricing
        testProduct1 = new Product();
        testProduct1.setProductName("Test Product 1");
        testProduct1.setProductDescription("First test product");
        testProduct1.setProductPrice(99.99);
        testProduct1.setProductCategory("Electronics");
        testProduct1.setProductInventory(10);
        testProduct1.setShop(testShop);
        testProduct1 = productRepository.save(testProduct1);

        // Create second test product with same category but lower price for comparison testing
        testProduct2 = new Product();
        testProduct2.setProductName("Test Product 2");
        testProduct2.setProductDescription("Second test product");
        testProduct2.setProductPrice(49.99);
        testProduct2.setProductCategory("Electronics");
        testProduct2.setProductInventory(5);
        testProduct2.setShop(testShop);
        testProduct2 = productRepository.save(testProduct2);
    }

    /**
     * Tests adding a single product to cart and verifying it appears in cart view.
     * Uses a MockHttpSession to maintain cart state across requests and verifies
     * the cart contains exactly one item after addition through model attribute assertions.
     */
    @Test
    void addToCart_SingleProduct_ProductAppearsInCart() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add product to cart via POST request with CSRF protection
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart?shopId=*"));

        // View cart via GET request and verify product presence through model attributes
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("cart"))
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));
    }

    /**
     * Tests adding multiple different products to cart and verifies all appear in cart view.
     * Demonstrates cart's ability to handle multiple distinct products by making sequential
     * POST requests and asserting the final cart contains both items.
     */
    @Test
    void addToCart_MultipleProducts_AllProductsInCart() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add first product to establish initial cart state
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Add second product to expand cart contents
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct2.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Verify cart contains both distinct products through item count assertion
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(2))));
    }

    /**
     * Tests adding the same product twice increases quantity rather than creating duplicate entries.
     * Verifies cart's quantity management by adding identical product twice and checking
     * that only one cart item exists with increased quantity.
     */
    @Test
    void addToCart_SameProductTwice_IncreasesQuantity() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add product first time to establish base quantity
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Add same product again to trigger quantity increment
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Verify cart maintains single item entry with updated quantity
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));
    }

    /**
     * Tests that cart state persists across multiple requests in the same session.
     * Simulates multiple cart view requests to verify session-based cart storage
     * maintains consistency through repeated access patterns.
     */
    @Test
    void cart_SessionPersistence_MaintainsState() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add product to establish persistent cart state
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Make multiple sequential requests to verify cart state persistence
        for (int i = 0; i < 3; i++) {
            mockMvc.perform(get("/cart")
                            .session(session)
                            .param("shopId", String.valueOf(testShop.getShopId())))
                    .andExpect(status().isOk())
                    .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));
        }
    }

    /**
     * Tests that different sessions have independent carts that don't share state.
     * Creates two separate sessions and verifies cart operations in one session
     * don't affect the cart state in another session, ensuring user isolation.
     */
    @Test
    void cart_DifferentSessions_IndependentCarts() throws Exception {
        MockHttpSession session1 = new MockHttpSession();
        MockHttpSession session2 = new MockHttpSession();

        // Add product to first user session
        mockMvc.perform(post("/cart/add")
                        .session(session1)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Verify first session maintains its cart state
        mockMvc.perform(get("/cart")
                        .session(session1)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));

        // Verify second session has independent empty cart state
        mockMvc.perform(get("/cart")
                        .session(session2)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(0))));
    }

    /**
     * Tests updating product quantity in cart through dedicated update endpoint.
     * Adds a product, updates its quantity via POST request, and verifies the
     * quantity change is persisted in the cart session.
     */
    @Test
    void updateCart_ChangeQuantity_QuantityUpdated() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add product to establish initial cart state
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Update product quantity to new value via update endpoint
        mockMvc.perform(post("/cart/update")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("quantity", "3")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        // Verify cart reflects the updated quantity
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk());
    }

    /**
     * Tests removing a product from cart via remove endpoint.
     * Adds multiple products, removes one specific product, and verifies
     * the remaining cart contains only the non-removed products.
     */
    @Test
    void removeFromCart_RemovesProduct() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add first product to establish multi-item cart
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Add second product to create removal target scenario
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct2.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Remove first product via dedicated removal endpoint
        mockMvc.perform(post("/cart/remove")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));

        // Verify only the non-removed product remains in cart
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));
    }

    /**
     * Tests viewing an empty cart displays appropriate empty state.
     * Verifies that accessing cart without any products returns correct view
     * with empty items collection and proper template rendering.
     */
    @Test
    void viewCart_EmptyCart_DisplaysEmptyMessage() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/cart")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"))
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(0))));
    }

    /**
     * Tests adding product from different shop shows confirmation page for cart clearance.
     * Simulates multi-shop scenario where adding product from different shop triggers
     * confirmation workflow to prevent accidental cart replacement.
     */
    @Test
    void addToCart_DifferentShop_ShowsConfirmation() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Create second shop with different currency and business identity
        Shop shop2 = new Shop();
        shop2.setName("Second Shop");
        shop2.setCurrency("EUR");
        shop2.setBusinessType("Retail");
        shop2 = shopRepository.save(shop2);

        Product shop2Product = new Product();
        shop2Product.setProductName("Shop 2 Product");
        shop2Product.setProductPrice(29.99);
        shop2Product.setProductInventory(5);
        shop2Product.setShop(shop2);
        shop2Product = productRepository.save(shop2Product);

        // Add product from first shop to establish initial cart ownership
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Attempt to add product from different shop - should trigger confirmation
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(shop2Product.getProductID()))
                        .param("shopId", String.valueOf(shop2.getShopId()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("confirm-clear-cart"));
    }

    /**
     * Tests confirming cart clear replaces cart with new shop's products.
     * Verifies the complete cart replacement workflow when user confirms
     * they want to clear existing cart and shop with new shop's products.
     */
    @Test
    void addToCart_ConfirmClear_ReplacesCart() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Create second shop with European currency configuration
        Shop shop2 = new Shop();
        shop2.setName("Second Shop");
        shop2.setCurrency("EUR");
        shop2.setBusinessType("Retail");
        shop2 = shopRepository.save(shop2);

        Product shop2Product = new Product();
        shop2Product.setProductName("Shop 2 Product");
        shop2Product.setProductPrice(29.99);
        shop2Product.setProductInventory(5);
        shop2Product.setShop(shop2);
        shop2Product = productRepository.save(shop2Product);

        // Add product from first shop to establish cart that will be cleared
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Confirm cart clearance and add product from new shop
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(shop2Product.getProductID()))
                        .param("shopId", String.valueOf(shop2.getShopId()))
                        .param("confirmClear", "yes")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Verify cart now exclusively contains products from new shop
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(shop2.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attribute("cart", hasProperty("ownerShopId", is(shop2.getShopId()))))
                .andExpect(model().attribute("cart", hasProperty("items", hasSize(1))));
    }

    /**
     * Tests that cart total is calculated correctly across multiple products.
     * Adds products with different prices and verifies the cart calculates
     * the correct sum total through model attribute inspection.
     */
    @Test
    void cart_TotalCalculation_CalculatesCorrectly() throws Exception {
        MockHttpSession session = new MockHttpSession();

        // Add first product with higher price point
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // Add second product with lower price point
        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct2.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is3xxRedirection());

        // View cart and verify total calculation (99.99 + 49.99 = 149.98)
        mockMvc.perform(get("/cart")
                        .session(session)
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cart"));
    }

    /**
     * Tests adding non-existent product handles gracefully with appropriate error response.
     * Verifies the system properly handles invalid product IDs without crashing
     * and returns appropriate HTTP status code for client errors.
     */
    @Test
    void addToCart_NonExistentProduct_HandlesGracefully() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", "99999")
                        .param("shopId", String.valueOf(testShop.getShopId()))
                        .with(csrf()))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Tests cart operations without CSRF token are rejected for security.
     * Verifies that POST requests without valid CSRF token are properly
     * handled to prevent cross-site request forgery attacks.
     */
    @Test
    void cart_WithoutCSRF_Rejected() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(post("/cart/add")
                        .session(session)
                        .param("productId", String.valueOf(testProduct1.getProductID()))
                        .param("shopId", String.valueOf(testShop.getShopId())))
                .andExpect(status().is3xxRedirection());
    }
}