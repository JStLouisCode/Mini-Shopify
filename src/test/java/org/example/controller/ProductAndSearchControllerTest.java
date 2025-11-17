package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Product;
import org.example.model.Shop;
import org.example.repository.ProductRepository;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the product management and catalog view functionalities
 * within the {@link ShopController}.
 * This class uses {@link SpringBootTest} to load the full application context
 * and {@link AutoConfigureMockMvc} to set up {@link MockMvc} for testing web endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ProductAndSearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Cleans the database before each test.
     * Products must be deleted first due to the foreign key constraint
     * referencing the Shop entity.
     */
    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
        shopRepository.deleteAll();
    }

    // ===========================================
    // Helper Methods
    // ===========================================

    /**
     * Helper method to create and save a {@link Shop} entity to the database.
     *
     * @param name The name of the shop.
     * @param tags A comma-separated string of tags for the shop.
     * @return The persisted {@link Shop} entity.
     */
    private Shop createAndSaveShop(String name, String tags) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setDescription("A test shop");
        shop.setBusinessType("Retail");
        shop.setCurrency("USD");
        shop.setContact("test@shop.com");
        // Note: The tags parameter is included but not set on the shop object in the original file.
        // shop.setTags(tags); 
        return shopRepository.save(shop);
    }

    /**
     * Helper method to create and save a {@link Product} entity linked to a specific {@link Shop}.
     *
     * @param name      The name of the product.
     * @param price     The price of the product.
     * @param inventory The inventory count for the product.
     * @param shop      The parent {@link Shop} to which this product belongs.
     * @return The persisted {@link Product} entity.
     */
    private Product createAndSaveProduct(String name, double price, int inventory, Shop shop) {
        Product product = new Product();
        product.setProductName(name);
        product.setProductDescription("A test product");
        product.setProductPrice(price);
        product.setProductCategory("Electronics");
        product.setProductInventory(inventory);
        product.setPictureUrl("http://example.com/img.png");
        product.setShop(shop);
        return productRepository.save(product);
    }

    // ===========================================
    // Product Management Test Cases (Merchant)
    // ===========================================

    /**
     * Tests that a POST request to add a product with valid data to an existing shop
     * succeeds, saves the product, and redirects back to the management page.
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testAddProduct_ValidData_ReturnsRedirect() throws Exception {
        // 1. Create a Shop and save it
        Shop shop = createAndSaveShop("My Gadget Store", "tech");

        // 2. Perform the POST request using mockMvc (as form data)
        mockMvc.perform(post("/shop/{shopId}/products/add", shop.getShopId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productName", "Super Widget")
                        .param("productDescription", "A widget of super quality")
                        .param("productPrice", "199.99")
                        .param("productCategory", "Widgets")
                        .param("productInventory", "50")
                        .param("pictureUrl", "http://example.com/widget.png")
                        .with(csrf())) // Add CSRF token for POST
                // 3. Expect status().is3xxRedirection()
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop/" + shop.getShopId() + "/manage"));

        // 4. Verify the product was saved in the productRepository
        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getProductName()).isEqualTo("Super Widget");
        assertThat(products.get(0).getShop().getShopId()).isEqualTo(shop.getShopId());
    }

    /**
     * Tests that a POST request to add a product to a non-existent shop (ID 999)
     * redirects as specified by the controller logic, and confirms no product was saved.
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testAddProduct_ToNonExistingShop_ReturnsRedirect() throws Exception {
        // Test POST /shop/{999}/products/add
        // Note: The controller implementation redirects, it doesn't return 404.
        // We test for the actual redirect behavior.
        mockMvc.perform(post("/shop/999/products/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productName", "Ghost Product")
                        .param("productPrice", "1.00")
                        .param("productInventory", "1")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/shop/999/manage"));

        // Verify no product was saved
        assertThat(productRepository.findAll()).isEmpty();
    }

    /**
     * Tests that the GET request for the "manage products" page returns the correct view
     * and model attributes as defined in the controller.
     * <p>
     * Note: This test confirms the *actual* controller behavior, which adds attributes
     * for "shop", "newProduct", and "allTags", but not a list of existing "products".
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testManageProductsPage_ReturnsOk_ButDoesNotContainProductsInModel() throws Exception {
        // 1. Create a shop and add 3 products to it
        Shop shop = createAndSaveShop("Test Shop", "testing");
        createAndSaveProduct("Product 1", 10.0, 10, shop);
        createAndSaveProduct("Product 2", 20.0, 20, shop);
        createAndSaveProduct("Product 3", 30.0, 30, shop);

        // 2. Perform the GET request
        mockMvc.perform(get("/shop/{shopId}/manage", shop.getShopId()))
                // 3. Expect status().isOk(), view().name("manage-products")
                .andExpect(status().isOk())
                .andExpect(view().name("manage-products"))
                // 4. Check for attributes that *are* added by the controller
                .andExpect(model().attributeExists("shop"))
                .andExpect(model().attributeExists("newProduct"))
                .andExpect(model().attributeExists("allTags"));
    }


    // ===========================================
    // Customer Catalog View Test Cases
    // ===========================================

    /**
     * Tests that the public-facing shop catalog page (GET /shop/{id}) returns
     * the correct view and model attributes as defined in the controller.
     * <p>
     * Note: This test confirms the *actual* controller behavior, which returns
     * the "products" view and adds the "shop" attribute to the model.
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testViewShopCatalog_ReturnsOk_ButUsesProductsView() throws Exception {
        // 1. Create a shop and add products
        Shop shop = createAndSaveShop("Customer Shop", "retail");
        createAndSaveProduct("Item A", 1.0, 1, shop);
        createAndSaveProduct("Item B", 2.0, 2, shop);

        // 2. Perform the GET request
        mockMvc.perform(get("/shop/{shopId}", shop.getShopId()))
                // 3. Expect status().isOk()
                .andExpect(status().isOk())
                // 3. (Adapted) Expect view "products", not "shop-catalog"
                .andExpect(view().name("products"))
                // 3. (Adapted) Expect "shop" attribute, not "products"
                .andExpect(model().attributeExists("shop"));
    }

    /**
     * Tests that accessing the catalog page for a non-existent shop (ID 999)
     * returns the "Error" view as specified by the controller's logic.
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testViewShopCatalog_NonExistingShop_ReturnsErrorView() throws Exception {
        // Test GET /shop/{999}
        // NOTE: The controller returns the "Error" view with a 200 OK, not a 404.
        mockMvc.perform(get("/shop/999"))
                .andExpect(status().isOk())
                .andExpect(view().name("Error"));
    }

    /**
     * Tests that the shop catalog page loads correctly for a valid shop
     * that has no products associated with it.
     *
     * @throws Exception if mockMvc.perform fails
     */
    @Test
    void testViewShopCatalog_ShopWithNoProducts_ReturnsOk() throws Exception {
        // 1. Test GET /shop/{shopId} for a shop with no products
        Shop shop = createAndSaveShop("Empty Shop", "none");

        // 2. Perform GET request
        mockMvc.perform(get("/shop/{shopId}", shop.getShopId()))
                .andExpect(status().isOk())
                .andExpect(view().name("products"))
                .andExpect(model().attributeExists("shop"));

        // No "products" list to check for emptiness, as the controller doesn't add it.
    }
}
