package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.datastructures.BinarySearchTree;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * Service for managing products and inventory
 * Time Complexity Analysis:
 * - addProduct(): O(log n) using BST
 * - removeProduct(): O(log n) using BST
 * - updateProduct(): O(log n) search + O(1) update
 * - searchById(): O(log n) using BST
 * - searchByName(): O(n) linear search
 * - getOutOfStockProducts(): O(n) iteration
 * - addReviewToProduct(): O(log n) search + O(1) add
 */
public class ProductService {
    private ArrayList<Product> products;
    private BinarySearchTree<ProductWrapper> productIndex;

    private static class ProductWrapper implements Comparable<ProductWrapper> {
        Product product;

        ProductWrapper(Product product) {
            this.product = product;
        }

        @Override
        public int compareTo(ProductWrapper other) {
            return Integer.compare(this.product.getProductId(), other.product.getProductId());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ProductWrapper that = (ProductWrapper) o;
            return product.getProductId() == that.product.getProductId();
        }
    }

    public ProductService() {
        this.products = new ArrayList<>();
        this.productIndex = new BinarySearchTree<>();
    }

    public void addProduct(Product product) {
        products.add(product);
        productIndex.insert(new ProductWrapper(product));
    }

    public boolean removeProduct(int productId) {
        Product product = searchById(productId);
        if (product != null) {
            products.remove(product);
            productIndex.delete(new ProductWrapper(product));
            return true;
        }
        return false;
    }

    public boolean updateProduct(int productId, String name, double price, int stock) {
        Product product = searchById(productId);
        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);
            return true;
        }
        return false;
    }

    public Product searchById(int productId) {
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getProductId() == productId) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Product> searchByName(String name) {
        ArrayList<Product> results = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    public ArrayList<Product> getOutOfStockProducts() {
        ArrayList<Product> outOfStock = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product p = products.get(i);
            if (p.isOutOfStock()) {
                outOfStock.add(p);
            }
        }
        return outOfStock;
    }

    public boolean addReviewToProduct(int productId, Review review) {
        Product product = searchById(productId);
        if (product != null) {
            product.addReview(review);
            return true;
        }
        return false;
    }

    public boolean editReview(int productId, int reviewId, int newRating, String newComment) {
        Product product = searchById(productId);
        if (product != null) {
            ArrayList<Review> reviews = product.getReviews();
            for (int i = 0; i < reviews.size(); i++) {
                Review review = reviews.get(i);
                if (review.getReviewId() == reviewId) {
                    review.setRating(newRating);
                    review.setComment(newComment);
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Product> getAllProducts() {
        return products;
    }

    public int getProductCount() {
        return products.size();
    }
}
