package org.example.model;

import org.example.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=true"
})
public class ProductPersistenceTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void whenSaveProduct_thenProductIsPersisted() {
        // Given
        Shop shop = createTestShop();
        Product product = createTestProduct(shop);

        // When
        Product savedProduct = productRepository.save(product);

        // Then
        assertThat(savedProduct.getProductID()).isNotNull();
        assertThat(savedProduct.getProductName()).isEqualTo("Test Laptop");
        assertThat(savedProduct.getProductInventory()).isEqualTo(10);
        assertThat(savedProduct.getShop().getShopId()).isEqualTo(shop.getShopId());
    }

    @Test
    public void whenFindById_thenReturnProduct() {
        // Given
        Shop shop = createTestShop();
        Product product = createTestProduct(shop);
        Product savedProduct = entityManager.persistAndFlush(product);
        entityManager.flush();
        entityManager.clear();

        // When
        Optional<Product> foundProduct = productRepository.findById(savedProduct.getProductID());

        // Then
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getProductName()).isEqualTo("Test Laptop");
    }

    @Test
    public void whenFindByShopId_thenReturnProducts() {
        // Given
        Shop shop1 = createTestShop("Shop 1");
        Shop shop2 = createTestShop("Shop 2");

        Product product1 = createTestProduct(shop1, "Product 1");
        Product product2 = createTestProduct(shop1, "Product 2");
        Product product3 = createTestProduct(shop2, "Product 3");

        entityManager.persistAndFlush(product1);
        entityManager.persistAndFlush(product2);
        entityManager.persistAndFlush(product3);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Product> shop1Products = productRepository.findByShopId(shop1.getShopId());
        List<Product> shop2Products = productRepository.findByShopId(shop2.getShopId());

        // Then
        assertThat(shop1Products).hasSize(2);
        assertThat(shop2Products).hasSize(1);
    }

    @Test
    public void whenFindByProductNameContaining_thenReturnMatchingProducts() {
        // Given
        Shop shop = createTestShop();
        Product laptop = createTestProduct(shop, "Gaming Laptop");
        Product mouse = createTestProduct(shop, "Wireless Mouse");

        entityManager.persistAndFlush(laptop);
        entityManager.persistAndFlush(mouse);
        entityManager.flush();
        entityManager.clear();

        // When
        List<Product> laptopProducts = productRepository.findByProductNameContainingIgnoreCase("laptop");

        // Then
        assertThat(laptopProducts).hasSize(1);
        assertThat(laptopProducts.get(0).getProductName()).isEqualTo("Gaming Laptop");
    }

    @Test
    public void whenUpdateProductQuantity_thenProductIsUpdated() {
        // Given
        Shop shop = createTestShop();
        Product product = createTestProduct(shop);
        Product savedProduct = entityManager.persistAndFlush(product);
        entityManager.flush();
        entityManager.clear();

        // When
        Product productToUpdate = productRepository.findById(savedProduct.getProductID()).orElseThrow();
        productToUpdate.setProductInventory(25);
        Product updatedProduct = productRepository.save(productToUpdate);

        // Then
        assertThat(updatedProduct.getProductInventory()).isEqualTo(25);
    }

    @Test
    public void whenDeleteProduct_thenProductIsRemoved() {
        // Given
        Shop shop = createTestShop();
        Product product = createTestProduct(shop);
        Product savedProduct = entityManager.persistAndFlush(product);
        entityManager.flush();
        entityManager.clear();

        // When
        productRepository.deleteById(savedProduct.getProductID());

        // Then
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getProductID());
        assertThat(deletedProduct).isEmpty();
    }

    // Helper methods
    private Shop createTestShop() {
        return createTestShop("Test Shop");
    }

    private Shop createTestShop(String name) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setDescription("Test Description");
        shop.setBusinessType("Retail");
        shop.setCurrency("USD");
        shop.setContact("test@shop.com");
        shop.setSocialMediaLinks("http://instagram.com/testshop");
        return entityManager.persistAndFlush(shop);
    }

    private Product createTestProduct(Shop shop) {
        return createTestProduct(shop, "Test Laptop");
    }

    private Product createTestProduct(Shop shop, String name) {
        Product product = new Product();
        product.setProductName(name);
        product.setProductDescription("This is a test product description");
        product.setProductPrice(999.99);
        product.setProductCategory("Electronics");
        product.setProductInventory(10);
        product.setPictureUrl("http://example.com/product.jpg");
        product.setShop(shop);
        return product;
    }
}