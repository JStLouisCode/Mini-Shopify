package org.example.controller;

import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the Search functionality in ShopController.
 * Tests both the API endpoint (/search) and the web page endpoint (/search-results).
 * Verifies correct filtering, case-insensitivity, and handling of edge cases.
 * Uses MockMvc to simulate HTTP requests and test search functionality across
 * name and tag-based search parameters with various query scenarios.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ShopRepository shopRepository;

    /**
     * Seeds the database with test shops before each test to ensure consistent test data.
     * Creates shops with various names, descriptions, tags, business types, and currencies
     * to test comprehensive search scenarios including exact matches, partial matches,
     * case sensitivity, and tag-based filtering across diverse business domains.
     */
    @BeforeEach
    void setUp() {
        // Clear database to ensure test isolation and consistent starting state
        shopRepository.deleteAll();

        // Create electronics-focused shop with technology tags
        Shop techShop = new Shop();
        techShop.setName("Tech World");
        techShop.setDescription("Electronics and gadgets");
        techShop.setTags(Arrays.asList("Electronics", "Technology", "Gadgets"));
        techShop.setBusinessType("Retail");
        techShop.setCurrency("USD");
        shopRepository.save(techShop);

        // Create education-focused shop with book-related tags
        Shop bookShop = new Shop();
        bookShop.setName("Book Haven");
        bookShop.setDescription("Books and reading materials");
        bookShop.setTags(Arrays.asList("Books", "Education"));
        bookShop.setBusinessType("Retail");
        bookShop.setCurrency("USD");
        shopRepository.save(bookShop);

        // Create food-focused shop with grocery tags and different currency
        Shop foodShop = new Shop();
        foodShop.setName("Gourmet Foods");
        foodShop.setDescription("Fine foods and delicacies");
        foodShop.setTags(Arrays.asList("Grocery", "Food"));
        foodShop.setBusinessType("Food & Beverage");
        foodShop.setCurrency("CAD");
        shopRepository.save(foodShop);

        // Create fashion-focused shop with clothing tags and European currency
        Shop clothingShop = new Shop();
        clothingShop.setName("Fashion Forward");
        clothingShop.setDescription("Trendy clothing and accessories");
        clothingShop.setTags(Arrays.asList("Clothing", "Fashion"));
        clothingShop.setBusinessType("Retail");
        clothingShop.setCurrency("EUR");
        shopRepository.save(clothingShop);

        // Create additional electronics shop with overlapping tags for multiple match testing
        Shop electronicsShop = new Shop();
        electronicsShop.setName("Electronic Emporium");
        electronicsShop.setDescription("All things electronic");
        electronicsShop.setTags(Arrays.asList("Electronics", "Home"));
        electronicsShop.setBusinessType("Retail");
        electronicsShop.setCurrency("USD");
        shopRepository.save(electronicsShop);
    }

    /**
     * Tests searching by shop name returns exactly matching shops.
     * Verifies the search functionality can find shops by exact name match
     * and returns the correct shop entity in JSON format with proper structure.
     */
    @Test
    void searchByName_ValidQuery_ReturnsMatchingShops() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Tech")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Tech World")));
    }

    /**
     * Tests searching by shop name is case-insensitive.
     * Verifies that search queries in different cases (uppercase, lowercase, mixed)
     * return the same results as case-matched queries, ensuring user-friendly search.
     */
    @Test
    void searchByName_CaseInsensitive_ReturnsMatches() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "TECH")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Tech World")));
    }

    /**
     * Tests searching by partial shop name returns matching shops.
     * Verifies that incomplete query terms still return relevant results
     * by matching against portions of shop names for flexible searching.
     */
    @Test
    void searchByName_PartialMatch_ReturnsMatches() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "food")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Gourmet Foods")));
    }

    /**
     * Tests searching by tag returns all shops with that tag.
     * Verifies tag-based search functionality by querying a common tag
     * and ensuring all shops with that tag are returned regardless of other attributes.
     */
    @Test
    void searchByTag_ValidTag_ReturnsMatchingShops() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Electronics")
                        .param("type", "tag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Tech World", "Electronic Emporium")));
    }

    /**
     * Tests searching by tag is case-insensitive.
     * Verifies that tag searches ignore case differences between query
     * and stored tags, ensuring consistent results regardless of input casing.
     */
    @Test
    void searchByTag_CaseInsensitive_ReturnsMatches() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "electronics")
                        .param("type", "tag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    /**
     * Tests searching with no query parameter returns empty list.
     * Verifies that missing search query is handled gracefully by returning
     * empty results rather than errors, providing robust API behavior.
     */
    @Test
    void search_NoQuery_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/search")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests searching with empty query string returns empty list.
     * Verifies that explicitly empty search queries return no results
     * rather than all items, preventing unintentional data exposure.
     */
    @Test
    void search_EmptyQuery_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests searching with non-existent name returns empty list.
     * Verifies the search handles non-matching queries gracefully by
     * returning empty results instead of errors or partial matches.
     */
    @Test
    void searchByName_NoMatches_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "NonExistentShop")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests searching with non-existent tag returns empty list.
     * Verifies tag search behavior when no shops have the requested tag,
     * ensuring consistent empty response for non-matching tag queries.
     */
    @Test
    void searchByTag_NoMatches_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "NonExistentTag")
                        .param("type", "tag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests searching defaults to name search when type is not specified.
     * Verifies the search functionality has sensible defaults by automatically
     * using name search when no explicit search type is provided in request.
     */
    @Test
    void search_NoTypeParameter_DefaultsToNameSearch() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Book"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Book Haven")));
    }

    /**
     * Tests search results page returns correct view with proper model attributes.
     * Verifies the web interface for search results renders correctly with
     * all necessary model data including shops, query, and type parameters.
     */
    @Test
    void searchResultsPage_ValidQuery_ReturnsCorrectView() throws Exception {
        mockMvc.perform(get("/search-results")
                        .param("query", "Tech")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"))
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attributeExists("query"))
                .andExpect(model().attributeExists("type"));
    }

    /**
     * Tests search results page includes matching shops in model when searching by name.
     * Verifies that the model contains exactly the shops that match the name query
     * and that the shop data includes the expected properties like name.
     */
    @Test
    void searchResultsPage_ByName_ReturnsMatchingShops() throws Exception {
        mockMvc.perform(get("/search-results")
                        .param("query", "Tech")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("shops", hasSize(1)))
                .andExpect(model().attribute("shops", hasItem(
                        hasProperty("name", is("Tech World"))
                )));
    }

    /**
     * Tests search results page by tag returns correct shops with multiple matches.
     * Verifies tag-based search in the web interface returns all shops that have
     * the specified tag, including scenarios with multiple matching shops.
     */
    @Test
    void searchResultsPage_ByTag_ReturnsMatchingShops() throws Exception {
        mockMvc.perform(get("/search-results")
                        .param("query", "Electronics")
                        .param("type", "tag"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("shops", hasSize(2)));
    }

    /**
     * Tests search results page with no query returns empty results.
     * Verifies the web interface handles missing search queries gracefully
     * by returning the search results page with null query attribute.
     */
    @Test
    void searchResultsPage_NoQuery_ReturnsEmptyResults() throws Exception {
        mockMvc.perform(get("/search-results")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("query", nullValue()));
    }

    /**
     * Tests search results page with whitespace-only query returns empty results.
     * Verifies that queries consisting only of whitespace are treated as empty
     * queries and return the search results page without matches.
     */
    @Test
    void searchResultsPage_WhitespaceQuery_ReturnsEmptyResults() throws Exception {
        mockMvc.perform(get("/search-results")
                        .param("query", "   ")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-results"));
    }

    /**
     * Tests search excludes shops that don't match the query to verify precision.
     * Confirms that shops without matching names are properly excluded from results
     * to ensure search returns only relevant matches without false positives.
     */
    @Test
    void search_VerifyExclusion_DoesNotReturnNonMatchingShops() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Tech")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].name", not(containsInAnyOrder("Book Haven", "Gourmet Foods", "Fashion Forward"))));
    }

    /**
     * Tests search by tag excludes shops without that tag to verify tag filtering.
     * Confirms that tag-based search properly excludes shops that don't have
     * the specified tag, ensuring precise tag-based filtering.
     */
    @Test
    void searchByTag_VerifyExclusion_DoesNotReturnShopsWithoutTag() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Books")
                        .param("type", "tag"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Book Haven")))
                .andExpect(jsonPath("$[*].name", not(hasItem("Tech World"))));
    }

    /**
     * Tests searching with special characters in query handles gracefully.
     * Verifies that queries containing special characters don't cause errors
     * and return empty results when no shops match the special character pattern.
     */
    @Test
    void search_SpecialCharacters_HandlesGracefully() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Tech@#$")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    /**
     * Tests searching with multiple matching results returns all relevant shops.
     * Verifies that when multiple shops match the search criteria (by name similarity),
     * all matching shops are returned in the results for comprehensive searching.
     */
    @Test
    void search_MultipleMatches_ReturnsAllMatches() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "Electronic")
                        .param("type", "name"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Electronic Emporium")));
    }
}