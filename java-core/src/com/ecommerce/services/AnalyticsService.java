package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.datastructures.BinarySearchTree;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * PHASE II: Analytics Service with BST-optimized queries
 * 
 * Time Complexity Analysis (Phase II):
 * - getTop3ProductsByRating(): O(n) - must check all products
 * - getCommonHighRatedProducts(): O(n * r) where n is products, r is reviews
 * - getCustomersWhoReviewedProduct(): O(n + c) where n is products, c is customers
 * 
 * Comparison with Phase I:
 * Similar complexities but benefits from faster lookups in other operations
 */
public class AnalyticsService {
    private ProductService productService;
    private CustomerService customerService;

    public AnalyticsService(ProductService productService, CustomerService customerService) {
        this.productService = productService;
        this.customerService = customerService;
    }

    /**
     * PHASE II REQUIREMENT: Get top 3 products by rating
     * Time Complexity: O(n) - must check all products
     * Phase I Complexity: O(n) with bubble sort
     * Note: Same complexity but cleaner implementation
     */
    public ArrayList<Product> getTop3ProductsByRating() {
        ArrayList<Product> allProducts = productService.getAllProducts();
        
        if (allProducts.size() == 0) {
            return new ArrayList<>();
        }

        // Filter products with reviews
        ArrayList<Product> productsWithReviews = new ArrayList<>();
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.getReviews().size() > 0) {
                productsWithReviews.add(p);
            }
        }

        // Sort by average rating (descending) using bubble sort
        for (int i = 0; i < productsWithReviews.size() - 1; i++) {
            for (int j = 0; j < productsWithReviews.size() - i - 1; j++) {
                if (productsWithReviews.get(j).getAverageRating() < 
                    productsWithReviews.get(j + 1).getAverageRating()) {
                    Product temp = productsWithReviews.get(j);
                    productsWithReviews.set(j, productsWithReviews.get(j + 1));
                    productsWithReviews.set(j + 1, temp);
                }
            }
        }

        // Return top 3
        ArrayList<Product> top3 = new ArrayList<>();
        int limit = Math.min(3, productsWithReviews.size());
        for (int i = 0; i < limit; i++) {
            top3.add(productsWithReviews.get(i));
        }

        return top3;
    }

    /**
     * PHASE II REQUIREMENT: Find common high-rated products between two customers
     * Time Complexity: O(n * r) where n is products, r is reviews per product
     * Phase I Complexity: O(n * r) - same
     */
    public ArrayList<Product> getCommonHighRatedProducts(int customerId1, int customerId2) {
        ArrayList<Product> allProducts = productService.getAllProducts();
        ArrayList<Product> commonProducts = new ArrayList<>();

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            ArrayList<Review> reviews = product.getReviews();

            boolean customer1Reviewed = false;
            boolean customer2Reviewed = false;
            double totalRating = 0.0;
            int count = 0;

            for (int j = 0; j < reviews.size(); j++) {
                Review review = reviews.get(j);
                if (review.getCustomerId() == customerId1) {
                    customer1Reviewed = true;
                    totalRating += review.getRating();
                    count++;
                } else if (review.getCustomerId() == customerId2) {
                    customer2Reviewed = true;
                    totalRating += review.getRating();
                    count++;
                }
            }

            if (customer1Reviewed && customer2Reviewed && count > 0) {
                double avgRating = totalRating / count;
                if (avgRating > 4.0) {
                    commonProducts.add(product);
                }
            }
        }

        return commonProducts;
    }

    /**
     * PHASE II REQUIREMENT: Get all customers who reviewed a product, sorted by rating
     * Time Complexity: O(r * log c) where r is reviews, c is customers
     * Phase I Complexity: O(r * n) - linear customer lookup for each review
     * IMPROVEMENT: O(r * n) -> O(r * log c)
     */
    public ArrayList<CustomerReviewInfo> getCustomersWhoReviewedProduct(int productId) {
        Product product = productService.searchById(productId);
        if (product == null) {
            return new ArrayList<>();
        }

        ArrayList<Review> reviews = product.getReviews();
        BinarySearchTree<Integer, CustomerReviewInfo> reviewBST = new BinarySearchTree<>();

        // Build BST sorted by rating (descending) and customer ID
        for (int i = 0; i < reviews.size(); i++) {
            Review review = reviews.get(i);
            Customer customer = customerService.searchCustomerById(review.getCustomerId());
            
            if (customer != null) {
                CustomerReviewInfo info = new CustomerReviewInfo(
                    customer.getCustomerId(),
                    customer.getName(),
                    customer.getEmail(),
                    review.getRating(),
                    review.getComment()
                );
                
                // Use negative rating for descending order, then customer ID for tie-breaking
                int sortKey = (-review.getRating() * 10000) + customer.getCustomerId();
                reviewBST.insert(sortKey, info);
            }
        }

        return reviewBST.inorderTraversal();
    }

    /**
     * Helper class for customer review information
     */
    public static class CustomerReviewInfo {
        private int customerId;
        private String customerName;
        private String customerEmail;
        private int rating;
        private String comment;

        public CustomerReviewInfo(int customerId, String customerName, String customerEmail, 
                                 int rating, String comment) {
            this.customerId = customerId;
            this.customerName = customerName;
            this.customerEmail = customerEmail;
            this.rating = rating;
            this.comment = comment;
        }

        public int getCustomerId() { return customerId; }
        public String getCustomerName() { return customerName; }
        public String getCustomerEmail() { return customerEmail; }
        public int getRating() { return rating; }
        public String getComment() { return comment; }

        @Override
        public String toString() {
            return "Customer: " + customerName + " (ID: " + customerId + ", Email: " + customerEmail + 
                   ") - Rating: " + rating + "/5 - Comment: " + comment;
        }
    }

    public String generateProductReport(Product product) {
        StringBuilder report = new StringBuilder();
        report.append("Product Report\n");
        report.append("==============\n");
        report.append("ID: ").append(product.getProductId()).append("\n");
        report.append("Name: ").append(product.getName()).append("\n");
        report.append("Price: $").append(String.format("%.2f", product.getPrice())).append("\n");
        report.append("Stock: ").append(product.getStock()).append("\n");
        report.append("Average Rating: ").append(String.format("%.2f", product.getAverageRating())).append("\n");
        report.append("Total Reviews: ").append(product.getReviews().size()).append("\n");
        report.append("Status: ").append(product.isOutOfStock() ? "OUT OF STOCK" : "In Stock").append("\n");
        
        return report.toString();
    }

    public String generateInventoryReport() {
        ArrayList<Product> allProducts = productService.getAllProducts();
        StringBuilder report = new StringBuilder();
        
        report.append("Inventory Report\n");
        report.append("================\n");
        report.append("Total Products: ").append(allProducts.size()).append("\n\n");
        
        int outOfStock = 0;
        int lowStock = 0;
        double totalValue = 0.0;
        
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.isOutOfStock()) {
                outOfStock++;
            } else if (p.getStock() < 10) {
                lowStock++;
            }
            totalValue += p.getPrice() * p.getStock();
        }
        
        report.append("Out of Stock: ").append(outOfStock).append("\n");
        report.append("Low Stock (<10): ").append(lowStock).append("\n");
        report.append("Total Inventory Value: $").append(String.format("%.2f", totalValue)).append("\n");
        
        return report.toString();
    }
}