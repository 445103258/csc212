package com.ecommerce;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.services.AnalyticsService;
import com.ecommerce.services.CustomerService;
import com.ecommerce.services.OrderService;
import com.ecommerce.services.ProductService;
import com.ecommerce.utils.CSVReader;
import java.time.LocalDate;

/**
 * PHASE II: E-Commerce System with Binary Search Trees
 * 
 * This demonstrates the transition from Phase I (Linear Data Structures) 
 * to Phase II (Logarithmic Data Structures - BST)
 * 
 * Key Improvements:
 * 1. Product search: O(n) -> O(log n)
 * 2. Customer search: O(n) -> O(log n)
 * 3. Order search: O(n) -> O(log n)
 * 4. Range queries: O(n) -> O(log n + k)
 * 5. Sorted traversals: O(n log n) -> O(n)
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("E-COMMERCE SYSTEM - PHASE II");
        System.out.println("Binary Search Tree Implementation");
        System.out.println("==============================================\n");

        // Initialize services with BST-based implementations
        ProductService productService = new ProductService();
        OrderService orderService = new OrderService();
        CustomerService customerService = new CustomerService(productService, orderService);
        AnalyticsService analyticsService = new AnalyticsService(productService, customerService);

        // Load data from CSV files
        System.out.println("Loading data from CSV files...\n");
        loadData(productService, customerService, orderService);

        // Demonstrate Phase II requirements
        demonstratePhaseIIRequirements(productService, customerService, orderService, analyticsService);
        
        // Show Big-O comparison
        printComplexityComparison();
    }

    private static void loadData(ProductService productService, CustomerService customerService, 
                                 OrderService orderService) {
        // Load products
        CSVReader.loadProducts("python-api/data/products.csv", productService);

        // Load customers
        CSVReader.loadCustomers("python-api/data/customers.csv", customerService);

        // Load orders
        CSVReader.loadOrders("python-api/data/orders.csv", orderService);

        // Load reviews
        CSVReader.loadReviews("python-api/data/reviews.csv", productService);
        
        System.out.println();
    }

    private static void demonstratePhaseIIRequirements(ProductService productService, 
                                                       CustomerService customerService,
                                                       OrderService orderService,
                                                       AnalyticsService analyticsService) {
        
        System.out.println("\n==============================================");
        System.out.println("PHASE II REQUIREMENTS DEMONSTRATION");
        System.out.println("==============================================\n");

        // 1. Insert/Update Product with O(log n)
        System.out.println("1. INSERT/UPDATE PRODUCT (O(log n))");
        System.out.println("-----------------------------------");
        Product newProduct = new Product(999, "Gaming Mouse", 79.99, 50);
        productService.addProduct(newProduct);
        System.out.println("✓ Added: " + newProduct);
        productService.updateProduct(999, "Gaming Mouse Pro", 89.99, 45);
        Product updated = productService.searchById(999);
        System.out.println("✓ Updated: " + updated);
        System.out.println();

        // 2. Search Product by ID with O(log n)
        System.out.println("2. SEARCH PRODUCT BY ID (O(log n))");
        System.out.println("-----------------------------------");
        Product found = productService.searchById(101);
        if (found != null) {
            System.out.println("✓ Found product ID 101: " + found);
        } else {
            System.out.println("✗ Product ID 101 not found");
        }
        System.out.println();

        // 3. Range Query by Price
        System.out.println("3. RANGE QUERY BY PRICE (O(log n + k))");
        System.out.println("---------------------------------------");
        ArrayList<Product> priceRange = productService.getProductsByPriceRange(20.0, 50.0);
        System.out.println("✓ Products in price range [$20.00 - $50.00]:");
        for (int i = 0; i < Math.min(5, priceRange.size()); i++) {
            Product p = priceRange.get(i);
            System.out.println("  - " + p.getName() + ": $" + p.getPrice());
        }
        System.out.println("  Total: " + priceRange.size() + " products");
        System.out.println();

        // 4. Customer Operations with O(log n)
        System.out.println("4. CUSTOMER SEARCH (O(log n))");
        System.out.println("------------------------------");
        Customer customer = customerService.searchCustomerById(1);
        if (customer != null) {
            System.out.println("✓ Found customer ID 1: " + customer.getName() + " (" + customer.getEmail() + ")");
        } else {
            System.out.println("✗ Customer ID 1 not found");
        }
        System.out.println();

        // 5. Customer Order History
        System.out.println("5. CUSTOMER ORDER HISTORY (O(k * log n))");
        System.out.println("-----------------------------------------");
        if (customer != null) {
            ArrayList<Order> orderHistory = customerService.getCustomerOrderHistory(1);
            System.out.println("✓ Order history for " + customer.getName() + ":");
            for (int i = 0; i < Math.min(3, orderHistory.size()); i++) {
                Order order = orderHistory.get(i);
                System.out.println("  - Order #" + order.getOrderId() + ": $" + 
                                 String.format("%.2f", order.getTotalPrice()) + 
                                 " on " + order.getOrderDate());
            }
        }
        System.out.println();

        // 6. ADVANCED QUERY: Orders Between Two Dates
        System.out.println("6. ORDERS BETWEEN TWO DATES (O(log n + k))");
        System.out.println("-------------------------------------------");
        LocalDate startDate = LocalDate.of(2024, 1, 1);
        LocalDate endDate = LocalDate.of(2024, 6, 30);
        ArrayList<Order> dateRangeOrders = orderService.getOrdersBetweenDates(startDate, endDate);
        System.out.println("✓ Orders between " + startDate + " and " + endDate + ":");
        System.out.println("  Total orders: " + dateRangeOrders.size());
        for (int i = 0; i < Math.min(3, dateRangeOrders.size()); i++) {
            Order order = dateRangeOrders.get(i);
            System.out.println("  - Order #" + order.getOrderId() + " on " + order.getOrderDate() + 
                             " - $" + String.format("%.2f", order.getTotalPrice()));
        }
        System.out.println();

        // 7. Top 3 Products by Rating
        System.out.println("7. TOP 3 PRODUCTS BY RATING");
        System.out.println("----------------------------");
        ArrayList<Product> top3 = analyticsService.getTop3ProductsByRating();
        System.out.println("✓ Top 3 highest rated products:");
        for (int i = 0; i < top3.size(); i++) {
            Product p = top3.get(i);
            System.out.println("  " + (i+1) + ". " + p.getName() + 
                             " - Rating: " + String.format("%.2f", p.getAverageRating()) + 
                             " (" + p.getReviews().size() + " reviews)");
        }
        System.out.println();

        // 8. Customers Sorted Alphabetically
        System.out.println("8. CUSTOMERS SORTED ALPHABETICALLY (O(n))");
        System.out.println("------------------------------------------");
        ArrayList<Customer> sortedCustomers = customerService.getAllCustomersSorted();
        System.out.println("✓ First 5 customers (alphabetically):");
        for (int i = 0; i < Math.min(5, sortedCustomers.size()); i++) {
            Customer c = sortedCustomers.get(i);
            System.out.println("  - " + c.getName() + " (ID: " + c.getCustomerId() + ")");
        }
        System.out.println();

        // 9. Customers Who Reviewed a Product (Sorted by Rating)
        System.out.println("9. CUSTOMERS WHO REVIEWED PRODUCT (Sorted by Rating)");
        System.out.println("-----------------------------------------------------");
        ArrayList<AnalyticsService.CustomerReviewInfo> reviewers = 
            analyticsService.getCustomersWhoReviewedProduct(101);
        System.out.println("✓ Customers who reviewed product ID 101:");
        for (int i = 0; i < Math.min(5, reviewers.size()); i++) {
            AnalyticsService.CustomerReviewInfo info = reviewers.get(i);
            System.out.println("  - " + info.getCustomerName() + " (Rating: " + 
                             info.getRating() + "/5)");
        }
        System.out.println();

        // 10. Common High-Rated Products Between Two Customers
        System.out.println("10. COMMON HIGH-RATED PRODUCTS (Rating > 4)");
        System.out.println("--------------------------------------------");
        ArrayList<Product> commonProducts = analyticsService.getCommonHighRatedProducts(1, 2);
        System.out.println("✓ Common high-rated products between Customer 1 and Customer 2:");
        if (commonProducts.size() == 0) {
            System.out.println("  No common high-rated products found.");
        } else {
            for (int i = 0; i < commonProducts.size(); i++) {
                Product p = commonProducts.get(i);
                System.out.println("  - " + p.getName() + " (Avg Rating: " + 
                                 String.format("%.2f", p.getAverageRating()) + ")");
            }
        }
        System.out.println();
    }

    private static void printComplexityComparison() {
        System.out.println("\n==============================================");
        System.out.println("BIG-O COMPLEXITY COMPARISON");
        System.out.println("==============================================\n");
        
        System.out.println("OPERATION                    | PHASE I (Linear) | PHASE II (BST)");
        System.out.println("----------------------------------------------------------");
        System.out.println("Add Product                  | O(1) amortized   | O(log n)");
        System.out.println("Search Product by ID         | O(n)             | O(log n) ✓");
        System.out.println("Update Product               | O(n)             | O(log n) ✓");
        System.out.println("Remove Product               | O(n)             | O(log n) ✓");
        System.out.println("Price Range Query            | O(n)             | O(log n + k) ✓");
        System.out.println("----------------------------------------------------------");
        System.out.println("Add Customer                 | O(1) amortized   | O(log n)");
        System.out.println("Search Customer by ID        | O(n)             | O(log n) ✓");
        System.out.println("Search Customer by Name      | O(n)             | O(log n) ✓");
        System.out.println("Get Customers Sorted         | O(n log n)       | O(n) ✓");
        System.out.println("----------------------------------------------------------");
        System.out.println("Add Order                    | O(1) amortized   | O(log n)");
        System.out.println("Search Order by ID           | O(n)             | O(log n) ✓");
        System.out.println("Cancel Order                 | O(n)             | O(log n) ✓");
        System.out.println("Orders Between Dates         | O(n)             | O(log n + k) ✓");
        System.out.println("----------------------------------------------------------");
        System.out.println("Get Customer Order History   | O(k * n)         | O(k * log n) ✓");
        System.out.println("Get Customers Who Reviewed   | O(r * n)         | O(r * log c) ✓");
        System.out.println("----------------------------------------------------------");
        
        System.out.println("\nKEY IMPROVEMENTS:");
        System.out.println("✓ Search operations: O(n) → O(log n)");
        System.out.println("✓ Range queries: O(n) → O(log n + k)");
        System.out.println("✓ Sorted traversals: O(n log n) → O(n)");
        System.out.println("✓ Overall system performance significantly improved for large datasets");
        
        System.out.println("\nLEGEND:");
        System.out.println("n = total number of items");
        System.out.println("k = number of results in range query");
        System.out.println("r = number of reviews");
        System.out.println("c = number of customers");
        System.out.println("✓ = Performance improvement over Phase I");
    }
}