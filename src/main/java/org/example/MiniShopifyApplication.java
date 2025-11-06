package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Mini-Shopify Spring Boot application.
 * This class bootstraps and launches the Spring Boot application.
 * The @SpringBootApplication annotation enables autoconfiguration and component scanning.
 */
@SpringBootApplication
public class MiniShopifyApplication {

    /**
     * Main method that serves as the application entry point.
     * Launches the Spring Boot application by running the Spring Application context.
     *
     * @param args command line arguments passed to the application
     *             (can be used for configuration, profiles, etc.)
     */
    public static void main(String[] args) {
        SpringApplication.run(MiniShopifyApplication.class, args);
    }

}