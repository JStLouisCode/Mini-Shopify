# Mini-Shopify

**Repository:** `https://github.com/JStLouisCode/Mini-Shopify`

This is a web application developed for the [Your Course Code, e.g., SYSC 4806] project. The primary objective is to apply modern software engineering methods and design principles to build a full-stack web application using the **Spring MVC** framework.

This application is an e-commerce platform that allows merchants to create their own digital shops, upload products, and manage inventory. Customers can browse these shops, search by name or tag, and (in future milestones) purchase products.

### Contributors
* Jonas Hallgrimsson
* Brock Fielder
* Ben Shvetz
* Tony Situ
* Jared St. Louis

---

## 🛠️ Tech Stack

* **Backend:** Spring Boot, Spring MVC, Spring Data JPA
* **Frontend:** Thymeleaf, HTML, CSS
* **Database:** [Your Database, e.g., PostgreSQL/H2]
* **Build:** Apache Maven
* **Deployment:** Docker, Amazon Web Services (AWS)
* **CI/CD:** GitHub Actions

---

## 🚀 Current Project Status (Milestone 1 Complete)

We have successfully completed **Milestone 1**. Our current functionality focuses on the **Merchant** experience and shop setup.

### Completed Features
* **Shop Creation:** Merchants can create a new shop by filling out a form with the shop's name, tags, description, and other details.
* **View All Shops:** A central page exists to view all shops currently in the system.
* **Shop Customization:** Merchants have the ability to edit and update the details of their existing shops.

### Project Backlog
Our full project backlog, user stories, and task breakdown are managed on our GitHub Projects Kanban board.

[**View our Project Kanban Board**](https://github.com/JStLouisCode/Mini-Shopify/projects)

---

## 🎯 Plan for Next Sprint (Milestone 2)

Our focus for Milestone 2 is to build out the **Product Management** and **Customer Browsing** features.

* **Product Management:** Implement functionality for merchants to upload products to their specific shop, including fields for product name, description, picture, and inventory count.
* **Customer Catalog:** Create the customer-facing view for a shop, allowing anyone to browse its product catalog.
* **Search Functionality:** Implement a search feature for customers to find shops, either by a direct name lookup or by searching via tags/categories.
* **Shopping Cart (Stretch Goal):** Begin development of the shopping cart functionality, allowing users to add products.

---

## 🏃 How to Run the Application

### Prerequisites
* Git
* Java JDK 17 (or newer)
* Apache Maven
* Docker (for containerized deployment)

### Option 1: Run Locally with Maven
1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/JStLouisCode/Mini-Shopify.git](https://github.com/JStLouisCode/Mini-Shopify.git)
    cd Mini-Shopify
    ```
2.  **Build and Test:**
    Use the Maven wrapper to compile, test, and package the application. This will produce a `.jar` file in the `target/` directory.
    ```sh
    ./mvnw clean install
    ```
3.  **Run the application:**
    ```sh
    java -jar target/[YOUR-APP-NAME-0.0.1-SNAPSHOT.jar]
    ```
    The application will be accessible at `http://localhost:8080`.

### Option 2: Run with Docker
1.  **Build the Docker image:**
    ```sh
    docker build -t mini-shopify .
    ```
2.  **Run the container:**
    (Note: This may require additional configuration for database linking, which should be in your `docker-compose.yml`)
    ```sh
    docker-compose up --build
    ```

---

## ☁️ Deployment

This application is configured for Continuous Integration (CI) using **GitHub Actions** and is deployed on **Amazon Web Services (AWS)**.

* **Continuous Integration:** The workflow (defined in `.github/workflows/`) automatically triggers on every push or pull request to the `main` branch. It builds the project, runs all unit and integration tests.
* **Continuous Deployment:** On a successful merge to `main`, the Docker image is built and pushed to AWS, where it is deployed.

**Live Application URL:** `[Your AWS Deployed App URL Here]`

---

## 🤝 Development Process

We follow Agile practices and a strict Git workflow as required by the course.

* **Git Flow:** All new features are developed on separate `feature/` branches. A Pull Request (PR) is opened to merge into the `main` branch.
* **Code Reviews:** All PRs must be reviewed and approved by at least **two** other team members before being merged.
* **Scrum:** We use **GitHub Issues** for our weekly scrum meetings. Each team member posts their progress, plans, and any blockers.
* **Backlog:** **GitHub Projects** serves as our official Kanban board to manage the product backlog and track the status of all tasks.

---

## 📈 System Design & Database Schema

Our design diagrams are version-controlled and updated as our application evolves.

### UML Model Diagram
This diagram shows the main entities (Models) in our application and their relationships.

![UML Model Diagram](path/to/your/model-diagram.png)

### Database Schema
This is the database schema automatically generated by the ORM (JPA/Hibernate) based on our models.

![Database Schema](path/to/your/db-schema.png)

### UML Sequence Diagram (Shop Creation)
This diagram shows the sequence of events when a merchant creates a new shop.

![UML Sequence Diagram](path/to/your/sequence-diagram.png)
