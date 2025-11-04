package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * Service for analytics and business intelligence queries
 * Time Complexity Analysis:
 * - getTop3ProductsByRating(): O(n) iteration + O(n log n) sorting = O(n log n)
 * - getCommonHighRatedProducts(): O(n*r) where n is products, r is reviews
 */
public class AnalyticsService {
    private ProductService productService;

    public AnalyticsService(ProductService productService) {
        this.productService = productService;
    }

    public ArrayList<Product> getTop3ProductsByRating() {
        ArrayList<Product> allProducts = productService.getAllProducts();
        
        if (allProducts.size() == 0) {
            return new ArrayList<>();
        }

        ArrayList<Product> productsWithReviews = new ArrayList<>();
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.getReviews().size() > 0) {
                productsWithReviews.add(p);
            }
        }

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

        ArrayList<Product> top3 = new ArrayList<>();
        int limit = Math.min(3, productsWithReviews.size());
        for (int i = 0; i < limit; i++) {
            top3.add(productsWithReviews.get(i));
        }

        return top3;
    }

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
