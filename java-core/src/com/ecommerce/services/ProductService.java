package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.datastructures.BinarySearchTree;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * PHASE II: Product Service using Binary Search Tree
 * 
 * Time Complexity Analysis (Phase II with BST):
 * - addProduct(): O(log n) - BST insertion
 * - removeProduct(): O(log n) - BST deletion
 * - updateProduct(): O(log n) - BST search + O(1) update
 * - searchById(): O(log n) - BST search
 * - searchByName(): O(n) - must traverse all products
 * - getProductsByPriceRange(): O(log n + k) - BST range query, k = results
 * - getOutOfStockProducts(): O(n) - must check all products
 * - addReviewToProduct(): O(log n) - BST search + O(1) add
 * 
 * Comparison with Phase I (Linear Data Structures):
 * Phase I - ArrayList:
 *   - addProduct(): O(1) amortized
 *   - searchById(): O(n) linear search
 *   - removeProduct(): O(n) search + O(n) removal
 *   - updateProduct(): O(n) search + O(1) update
 * 
 * Phase II - BST:
 *   - addProduct(): O(log n) 
 *   - searchById(): O(log n) - IMPROVED from O(n)
 *   - removeProduct(): O(log n) - IMPROVED from O(n)
 *   - updateProduct(): O(log n) - IMPROVED from O(n)
 */
public class ProductService {
    // Phase II: BST for O(log n) operations
    private BinarySearchTree<Integer, Product> productBST;  // Key: productId
    private BinarySearchTree<Double, ArrayList<Product>> priceIndex;  // Key: price, for range queries
    
    public ProductService() {
        this.productBST = new BinarySearchTree<>();
        this.priceIndex = new BinarySearchTree<>();
    }

    /**
     * Add a new product
     * Time Complexity: O(log n) - BST insertion
     * Phase I Complexity: O(1) amortized (ArrayList add)
     */
    public void addProduct(Product product) {
        productBST.insert(product.getProductId(), product);
        
        // Also index by price for range queries
        ArrayList<Product> productsAtPrice = priceIndex.search(product.getPrice());
        if (productsAtPrice == null) {
            productsAtPrice = new ArrayList<>();
        }
        productsAtPrice.add(product);
        priceIndex.insert(product.getPrice(), productsAtPrice);
    }

    /**
     * Remove a product by ID
     * Time Complexity: O(log n) - BST deletion
     * Phase I Complexity: O(n) - linear search + O(n) removal
     */
    public boolean removeProduct(int productId) {
        Product product = searchById(productId);
        if (product != null) {
            productBST.delete(productId);
            
            // Remove from price index
            ArrayList<Product> productsAtPrice = priceIndex.search(product.getPrice());
            if (productsAtPrice != null) {
                productsAtPrice.remove(product);
                if (productsAtPrice.isEmpty()) {
                    priceIndex.delete(product.getPrice());
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Update product details
     * Time Complexity: O(log n) - BST search + O(1) update
     * Phase I Complexity: O(n) - linear search + O(1) update
     */
    public boolean updateProduct(int productId, String name, double price, int stock) {
        Product product = searchById(productId);
        if (product != null) {
            // Remove from old price index
            ArrayList<Product> oldPriceProducts = priceIndex.search(product.getPrice());
            if (oldPriceProducts != null) {
                oldPriceProducts.remove(product);
                if (oldPriceProducts.isEmpty()) {
                    priceIndex.delete(product.getPrice());
                }
            }
            
            // Update product
            product.setName(name);
            product.setPrice(price);
            product.setStock(stock);
            
            // Add to new price index
            ArrayList<Product> newPriceProducts = priceIndex.search(price);
            if (newPriceProducts == null) {
                newPriceProducts = new ArrayList<>();
            }
            newPriceProducts.add(product);
            priceIndex.insert(price, newPriceProducts);
            
            return true;
        }
        return false;
    }

    /**
     * Search product by ID using BST
     * Time Complexity: O(log n) - BST search
     * Phase I Complexity: O(n) - linear search through ArrayList
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public Product searchById(int productId) {
        return productBST.search(productId);
    }

    /**
     * Search products by name (partial match)
     * Time Complexity: O(n) - must check all products
     * Phase I Complexity: O(n) - same
     * Note: No improvement possible without additional indexing
     */
    public ArrayList<Product> searchByName(String name) {
        ArrayList<Product> results = new ArrayList<>();
        ArrayList<Product> allProducts = productBST.inorderTraversal();
        
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.getName().toLowerCase().contains(name.toLowerCase())) {
                results.add(p);
            }
        }
        return results;
    }

    /**
     * PHASE II REQUIREMENT: Range Query by Price
     * Get all products within price range [minPrice, maxPrice]
     * Time Complexity: O(log n + k) where k is number of results
     * Phase I Complexity: O(n) - must check all products
     * IMPROVEMENT: More efficient for small result sets
     */
    public ArrayList<Product> getProductsByPriceRange(double minPrice, double maxPrice) {
        ArrayList<Product> result = new ArrayList<>();
        ArrayList<ArrayList<Product>> priceGroups = priceIndex.rangeQuery(minPrice, maxPrice);
        
        for (int i = 0; i < priceGroups.size(); i++) {
            ArrayList<Product> group = priceGroups.get(i);
            for (int j = 0; j < group.size(); j++) {
                result.add(group.get(j));
            }
        }
        
        return result;
    }

    /**
     * Get out of stock products
     * Time Complexity: O(n) - must check all products
     * Phase I Complexity: O(n) - same
     */
    public ArrayList<Product> getOutOfStockProducts() {
        ArrayList<Product> outOfStock = new ArrayList<>();
        ArrayList<Product> allProducts = productBST.inorderTraversal();
        
        for (int i = 0; i < allProducts.size(); i++) {
            Product p = allProducts.get(i);
            if (p.isOutOfStock()) {
                outOfStock.add(p);
            }
        }
        return outOfStock;
    }

    /**
     * Add review to product
     * Time Complexity: O(log n) - BST search + O(1) add
     * Phase I Complexity: O(n) - linear search + O(1) add
     */
    public boolean addReviewToProduct(int productId, Review review) {
        Product product = searchById(productId);
        if (product != null) {
            product.addReview(review);
            return true;
        }
        return false;
    }

    /**
     * Edit existing review
     * Time Complexity: O(log n + r) where r is reviews per product
     * Phase I Complexity: O(n + r)
     */
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

    /**
     * Get all products in sorted order by ID
     * Time Complexity: O(n) - inorder traversal
     * Phase I Complexity: O(n) - iterate ArrayList
     */
    public ArrayList<Product> getAllProducts() {
        return productBST.inorderTraversal();
    }

    /**
     * Get all products sorted by ID (guaranteed by BST inorder traversal)
     * Time Complexity: O(n)
     */
    public ArrayList<Product> getAllProductsSorted() {
        return productBST.inorderTraversal();
    }

    public int getProductCount() {
        return productBST.size();
    }
}