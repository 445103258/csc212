package com.ecommerce;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.*;
import com.ecommerce.services.*;
import com.ecommerce.utils.CSVReader;
import java.time.LocalDate;

/**
 * Main application demonstrating all functionality with complexity analysis
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("=== E-Commerce Inventory & Order Management System ===\n");
        
        ProductService productService = new ProductService();
        OrderService orderService = new OrderService();
        CustomerService customerService = new CustomerService(productService, orderService);
        AnalyticsService analyticsService = new AnalyticsService(productService);
        
        String dataPath = "../python-api/data/";
        
        System.out.println("Loading data from CSV files...\n");
        CSVReader.loadProducts(dataPath + "products.csv", productService);
        CSVReader.loadCustomers(dataPath + "customers.csv", customerService);
        CSVReader.loadOrders(dataPath + "orders.csv", orderService);
        CSVReader.loadReviews(dataPath + "reviews.csv", productService);
        
        System.out.println("\n=== Demonstrating Core Functionality ===\n");
        
        demonstrateProductOperations(productService);
        demonstrateCustomerOperations(customerService, productService);
        demonstrateOrderOperations(orderService, customerService, productService);
        demonstrateReviewOperations(productService, customerService);
        demonstrateAnalytics(analyticsService, customerService, orderService);
        
        System.out.println("\n=== Time Complexity Analysis ===\n");
        printComplexityAnalysis();
    }
    
    private static void demonstrateProductOperations(ProductService productService) {
        System.out.println("--- Product Operations ---");
        
        System.out.println("\n1. Add New Product:");
        Product newProduct = new Product(102, "Wireless Mouse", 29.99, 50);
        productService.addProduct(newProduct);
        System.out.println("Added: " + newProduct);
        
        System.out.println("\n2. Search Product by ID (101):");
        Product found = productService.searchById(101);
        System.out.println(found != null ? found : "Not found");
        
        System.out.println("\n3. Search Products by Name ('Laptop'):");
        ArrayList<Product> searchResults = productService.searchByName("Laptop");
        for (int i = 0; i < searchResults.size(); i++) {
            System.out.println("  " + searchResults.get(i));
        }
        
        System.out.println("\n4. Update Product:");
        productService.updateProduct(102, "Wireless Mouse Pro", 34.99, 45);
        System.out.println("Updated: " + productService.searchById(102));
        
        System.out.println("\n5. Track Out-of-Stock Products:");
        ArrayList<Product> outOfStock = productService.getOutOfStockProducts();
        System.out.println("Out of stock products: " + outOfStock.size());
        
        System.out.println();
    }
    
    private static void demonstrateCustomerOperations(CustomerService customerService, 
                                                     ProductService productService) {
        System.out.println("--- Customer Operations ---");
        
        System.out.println("\n1. Register New Customer:");
        Customer newCustomer = new Customer(202, "Bob Smith", "bob.smith@example.com");
        customerService.registerCustomer(newCustomer);
        System.out.println("Registered: " + newCustomer);
        
        System.out.println("\n2. Place Order for Customer:");
        ArrayList<Integer> productIds = new ArrayList<>();
        productIds.add(101);
        productIds.add(102);
        Order newOrder = customerService.placeOrder(201, productIds);
        if (newOrder != null) {
            System.out.println("Order placed: " + newOrder);
        } else {
            System.out.println("Failed to place order");
        }
        
        System.out.println("\n3. View Customer Order History:");
        ArrayList<Order> orderHistory = customerService.getCustomerOrderHistory(201);
        System.out.println("Customer 201 has " + orderHistory.size() + " orders:");
        for (int i = 0; i < orderHistory.size(); i++) {
            System.out.println("  " + orderHistory.get(i));
        }
        
        System.out.println();
    }
    
    private static void demonstrateOrderOperations(OrderService orderService,
                                                   CustomerService customerService,
                                                   ProductService productService) {
        System.out.println("--- Order Operations ---");
        
        System.out.println("\n1. Search Order by ID:");
        Order order = orderService.searchOrderById(301);
        System.out.println(order != null ? order : "Not found");
        
        System.out.println("\n2. Update Order Status:");
        boolean updated = orderService.updateOrderStatus(301, Order.OrderStatus.SHIPPED);
        System.out.println("Status updated: " + updated);
        if (updated) {
            System.out.println("New status: " + orderService.searchOrderById(301));
        }
        
        System.out.println("\n3. Get Orders Between Dates:");
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 12, 31);
        ArrayList<Order> ordersInRange = orderService.getOrdersBetweenDates(startDate, endDate);
        System.out.println("Orders between " + startDate + " and " + endDate + ": " + ordersInRange.size());
        for (int i = 0; i < ordersInRange.size(); i++) {
            System.out.println("  " + ordersInRange.get(i));
        }
        
        System.out.println();
    }
    
    private static void demonstrateReviewOperations(ProductService productService,
                                                    CustomerService customerService) {
        System.out.println("--- Review Operations ---");
        
        System.out.println("\n1. Add Review to Product:");
        Review newReview = new Review(402, 102, 201, 4, "Great mouse, very responsive!");
        productService.addReviewToProduct(102, newReview);
        System.out.println("Added review: " + newReview);
        
        System.out.println("\n2. Edit Review:");
        boolean edited = productService.editReview(102, 402, 5, "Excellent mouse, highly recommend!");
        System.out.println("Review edited: " + edited);
        
        System.out.println("\n3. Get Average Rating for Product:");
        Product product = productService.searchById(101);
        if (product != null) {
            System.out.println("Product: " + product.getName());
            System.out.println("Average Rating: " + String.format("%.2f", product.getAverageRating()));
            System.out.println("Total Reviews: " + product.getReviews().size());
        }
        
        System.out.println("\n4. Extract Reviews from Specific Customer:");
        ArrayList<Review> customerReviews = customerService.getCustomerReviews(201);
        System.out.println("Customer 201 has written " + customerReviews.size() + " reviews:");
        for (int i = 0; i < customerReviews.size(); i++) {
            System.out.println("  " + customerReviews.get(i));
        }
        
        System.out.println();
    }
    
    private static void demonstrateAnalytics(AnalyticsService analyticsService,
                                            CustomerService customerService,
                                            OrderService orderService) {
        System.out.println("--- Analytics & Business Intelligence ---");
        
        System.out.println("\n1. Top 3 Products by Average Rating:");
        ArrayList<Product> top3 = analyticsService.getTop3ProductsByRating();
        for (int i = 0; i < top3.size(); i++) {
            Product p = top3.get(i);
            System.out.println("  #" + (i + 1) + ": " + p.getName() + 
                             " (Rating: " + String.format("%.2f", p.getAverageRating()) + ")");
        }
        
        System.out.println("\n2. Common High-Rated Products (Rating > 4) between Two Customers:");
        ArrayList<Product> commonProducts = analyticsService.getCommonHighRatedProducts(201, 202);
        System.out.println("Common products between customers 201 and 202: " + commonProducts.size());
        for (int i = 0; i < commonProducts.size(); i++) {
            System.out.println("  " + commonProducts.get(i).getName());
        }
        
        System.out.println("\n3. Inventory Report:");
        System.out.println(analyticsService.generateInventoryReport());
        
        System.out.println();
    }
    
    private static void printComplexityAnalysis() {
        System.out.println("DATA STRUCTURE OPERATIONS:");
        System.out.println("ArrayList:");
        System.out.println("  - add(): O(1) amortized, O(n) worst case");
        System.out.println("  - get(): O(1)");
        System.out.println("  - remove(): O(n)");
        System.out.println();
        
        System.out.println("LinkedList:");
        System.out.println("  - addFirst/addLast(): O(1)");
        System.out.println("  - removeFirst/removeLast(): O(1)");
        System.out.println("  - get(): O(n)");
        System.out.println("  - remove(element): O(n)");
        System.out.println();
        
        System.out.println("Stack:");
        System.out.println("  - push(): O(1)");
        System.out.println("  - pop(): O(1)");
        System.out.println("  - peek(): O(1)");
        System.out.println();
        
        System.out.println("Queue:");
        System.out.println("  - enqueue(): O(1)");
        System.out.println("  - dequeue(): O(1)");
        System.out.println("  - peek(): O(1)");
        System.out.println();
        
        System.out.println("Binary Search Tree:");
        System.out.println("  - insert(): O(log n) average, O(n) worst");
        System.out.println("  - search(): O(log n) average, O(n) worst");
        System.out.println("  - delete(): O(log n) average, O(n) worst");
        System.out.println();
        
        System.out.println("BUSINESS OPERATIONS:");
        System.out.println("Product Service:");
        System.out.println("  - addProduct(): O(log n)");
        System.out.println("  - searchById(): O(n)");
        System.out.println("  - searchByName(): O(n)");
        System.out.println("  - getOutOfStockProducts(): O(n)");
        System.out.println();
        
        System.out.println("Customer Service:");
        System.out.println("  - registerCustomer(): O(1)");
        System.out.println("  - placeOrder(): O(n) for validation");
        System.out.println("  - getCustomerReviews(): O(n*r) where r = reviews per product");
        System.out.println();
        
        System.out.println("Order Service:");
        System.out.println("  - createOrder(): O(1)");
        System.out.println("  - searchOrderById(): O(n)");
        System.out.println("  - getOrdersBetweenDates(): O(n)");
        System.out.println();
        
        System.out.println("Analytics Service:");
        System.out.println("  - getTop3ProductsByRating(): O(n log n)");
        System.out.println("  - getCommonHighRatedProducts(): O(n*r)");
        System.out.println();
        
        System.out.println("SPACE COMPLEXITY:");
        System.out.println("  - All data structures: O(n) where n is number of elements");
        System.out.println("  - System total: O(P + C + O + R) where:");
        System.out.println("    P = products, C = customers, O = orders, R = reviews");
    }
}
