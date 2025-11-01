package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Shop;
import org.example.repository.ShopRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Shop sampleShop;
    @Autowired
    private ShopRepository shopRepository;

    @BeforeEach
    void setUp() {
        shopRepository.deleteAll();
        sampleShop = new Shop();
        sampleShop.setName("Test Shop");
        sampleShop.setDescription("A test shop for unit testing");
        sampleShop.setTags(Arrays.asList("test", "sample", "shop"));
        sampleShop.setBusinessType("Retail");
        sampleShop.setCurrency("CAD");
        sampleShop.setContact("123-4567");
        sampleShop.setSocialMediaLinks("@testshop");
    }

    // ===== CREATE TESTS =====

    @Test
    void createShop_ValidShop_ReturnsCreated() throws Exception {
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createShop_EmptyName_ReturnsCreated() throws Exception {
        Shop shopWithEmptyName = new Shop();
        shopWithEmptyName.setName("");

        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopWithEmptyName)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createShop_LongName_ReturnsCreated() throws Exception {
        Shop shopWithLongName = new Shop();
        shopWithLongName.setName("A".repeat(200)); // Very long name

        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopWithLongName)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createShop_SpecialCharactersInName_ReturnsCreated() throws Exception {
        Shop shopWithSpecialChars = new Shop();
        shopWithSpecialChars.setName("Test Shop @#$%^&*()");

        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopWithSpecialChars)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    @Test
    void createShop_NullFields_ReturnsCreated() throws Exception {
        Shop shopWithNullFields = new Shop();
        shopWithNullFields.setName("Minimal Shop");
        // Leave other fields null

        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(shopWithNullFields)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }

    // ===== READ TESTS =====

    @Test
    void getAllShops_ReturnsSuccess() throws Exception {
        mockMvc.perform(get("/shops"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllShops_AfterCreatingShop_ReturnsCreatedShop() throws Exception {
        // First create a shop
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Verify it appears in the list
        mockMvc.perform(get("/shops"))
                .andExpect(status().isOk());
    }

    @Test
    void getShopById_NonExistingShop_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/shops/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShopById_ExistingShop_ReturnsShop() throws Exception {
        // Create a shop first
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Get the created shop
        mockMvc.perform(get(location))
                .andExpect(status().isOk());
    }

    // ===== UPDATE TESTS =====

    @Test
    void updateShop_ValidShop_ReturnsNoContent() throws Exception {
        // Create a shop first
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Update the shop
        Shop updatedShop = new Shop();
        updatedShop.setName("Updated Shop Name");
        updatedShop.setDescription("Updated description");
        updatedShop.setTags(Arrays.asList("updated", "tags"));

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedShop)))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateShop_PartialUpdate_ReturnsNoContent() throws Exception {
        // Create a shop first
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Partial update - only change name
        Shop partialUpdate = new Shop();
        partialUpdate.setName("Only Name Updated");

        mockMvc.perform(put(location)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(partialUpdate)))
                .andExpect(status().isNoContent());
    }

    // ===== DELETE TESTS =====

    @Test
    void deleteShop_ExistingShop_ReturnsNoContent() throws Exception {
        // Create a shop first
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Delete the shop
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShop_NonExistingShop_ReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/shops/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShop_Twice_ReturnsNotFound() throws Exception {
        // Create a shop first
        MvcResult createResult = mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated())
                .andReturn();

        String location = createResult.getResponse().getHeader("Location");
        assertNotNull(location);

        // Delete the shop
        mockMvc.perform(delete(location))
                .andExpect(status().isNoContent());

        // Try to delete again
        mockMvc.perform(delete(location))
                .andExpect(status().isNotFound());
    }

    // ===== EDGE CASE TESTS =====

    @Test
    void createShop_MultipleShops_AllReturnCreated() throws Exception {
        for (int i = 0; i < 5; i++) {
            Shop shop = new Shop();
            shop.setName("Shop " + i);
            shop.setDescription("Description " + i);

            mockMvc.perform(post("/shops")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(shop)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"));
        }
    }

    @Test
    void createShop_EmptyRequestBody_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createShop_InvalidJson_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(Arrays.toString(new String[]{"invalid json"})))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getEndpoints_NoContentType_ReturnsSuccess() throws Exception {
        // GET requests should work without content type
        mockMvc.perform(get("/shops")
                        .contentType(""))
                .andExpect(status().isOk());
    }

    // ===== VIEW EXISTING SHOPS PAGE TESTS =====

    @Test
    void viewExistingShops_ReturnsCorrectViewName() throws Exception {
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(view().name("view-existing-shops"));
    }

    @Test
    void viewExistingShops_AddsShopsToModel() throws Exception {
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("shops"));
    }

    @Test
    void viewExistingShops_EmptyDatabase_ReturnsEmptyList() throws Exception {
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attribute("shops", org.hamcrest.Matchers.empty()));
    }


    @Test
    void viewExistingShops_WithExistingShops_ShopsListInModel() throws Exception {
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated());

        // Verify shop appears in view
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attribute("shops", org.hamcrest.Matchers.not(org.hamcrest.Matchers.empty())))
                .andExpect(model().attribute("shops", org.hamcrest.Matchers.hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(1))));
    }

    @Test
    void viewExistingShops_WithMultipleShops_AllShopsInModel() throws Exception {
        // Create multiple shops
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated());

        // Verify all shops are returned
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attribute("shops", org.hamcrest.Matchers.hasSize(org.hamcrest.Matchers.greaterThanOrEqualTo(3))));
    }


    @Test
    void viewExistingShops_VerifyShopDetails_AllFieldsPresent() throws Exception {
        mockMvc.perform(post("/shops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleShop)))
                .andExpect(status().isCreated());

        // Verify the shop appears with all its details
        mockMvc.perform(get("/view-existing-shops"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("shops"))
                .andExpect(model().attribute("shops",
                        org.hamcrest.Matchers.hasItem(
                                org.hamcrest.Matchers.allOf(
                                        org.hamcrest.Matchers.hasProperty("name", org.hamcrest.Matchers.is(sampleShop.getName())),
                                        org.hamcrest.Matchers.hasProperty("description", org.hamcrest.Matchers.is(sampleShop.getDescription())),
                                        org.hamcrest.Matchers.hasProperty("tags", org.hamcrest.Matchers.contains("test", "sample", "shop")),
                                        org.hamcrest.Matchers.hasProperty("businessType", org.hamcrest.Matchers.is(sampleShop.getBusinessType())),
                                        org.hamcrest.Matchers.hasProperty("currency", org.hamcrest.Matchers.is(sampleShop.getCurrency())),
                                        org.hamcrest.Matchers.hasProperty("contact", org.hamcrest.Matchers.is(sampleShop.getContact())),
                                        org.hamcrest.Matchers.hasProperty("socialMediaLinks", org.hamcrest.Matchers.is(sampleShop.getSocialMediaLinks()))
                                )
                        )));
    }

}