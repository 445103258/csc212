package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.datastructures.BinarySearchTree;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * PHASE II: Customer Service using Binary Search Tree
 * 
 * Time Complexity Analysis (Phase II with BST):
 * - registerCustomer(): O(log n) - BST insertion
 * - searchCustomerById(): O(log n) - BST search
 * - searchCustomerByName(): O(log n) - BST search by name key
 * - placeOrder(): O(m * log n) - m products, each O(log n) lookup
 * - getCustomerOrderHistory(): O(k * log n) - k orders, each O(log n) lookup
 * - getAllCustomersSorted(): O(n) - inorder traversal (alphabetically sorted)
 * 
 * Comparison with Phase I:
 * Phase I - ArrayList:
 *   - registerCustomer(): O(1) amortized
 *   - searchCustomerById(): O(n) linear search
 *   - searchCustomerByName(): O(n) linear search
 * 
 * Phase II - BST:
 *   - registerCustomer(): O(log n)
 *   - searchCustomerById(): O(log n) - IMPROVED from O(n)
 *   - searchCustomerByName(): O(log n) - IMPROVED from O(n)
 */
public class CustomerService {
    // Phase II: BST for O(log n) operations
    private BinarySearchTree<Integer, Customer> customerIdBST;  // Key: customerId
    private BinarySearchTree<String, Customer> customerNameBST;  // Key: customer name (for alphabetical sorting)
    private ProductService productService;
    private OrderService orderService;

    public CustomerService(ProductService productService, OrderService orderService) {
        this.customerIdBST = new BinarySearchTree<>();
        this.customerNameBST = new BinarySearchTree<>();
        this.productService = productService;
        this.orderService = orderService;
    }

    /**
     * Register a new customer
     * Time Complexity: O(log n) - two BST insertions
     * Phase I Complexity: O(1) - ArrayList add
     */
    public void registerCustomer(Customer customer) {
        customerIdBST.insert(customer.getCustomerId(), customer);
        customerNameBST.insert(customer.getName(), customer);
    }

    /**
     * Search customer by ID using BST
     * Time Complexity: O(log n) - BST search
     * Phase I Complexity: O(n) - linear search
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public Customer searchCustomerById(int customerId) {
        return customerIdBST.search(customerId);
    }

    /**
     * Search customer by name using BST
     * Time Complexity: O(log n) - BST search
     * Phase I Complexity: O(n) - linear search
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public Customer searchCustomerByName(String name) {
        return customerNameBST.search(name);
    }

    /**
     * Place an order for a customer
     * Time Complexity: O(m * log n) where m is number of products in order
     * Phase I Complexity: O(m * n) - linear search for each product
     * IMPROVEMENT: O(m * n) -> O(m * log n)
     */
    public Order placeOrder(int customerId, ArrayList<Integer> productIds) {
        Customer customer = searchCustomerById(customerId);
        if (customer == null) {
            return null;
        }

        double totalPrice = 0.0;
        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.searchById(productIds.get(i));
            if (product == null || product.getStock() < 1) {
                return null;
            }
            totalPrice += product.getPrice();
        }

        int orderId = orderService.getOrderCount() + 1;
        Order order = new Order(orderId, customerId, productIds, totalPrice, 
                               java.time.LocalDate.now(), Order.OrderStatus.PENDING);
        
        orderService.createOrder(order);
        customer.addOrder(orderId);

        for (int i = 0; i < productIds.size(); i++) {
            Product product = productService.searchById(productIds.get(i));
            product.decreaseStock(1);
        }

        return order;
    }

    /**
     * Get customer order history
     * Time Complexity: O(k * log n) where k is number of orders
     * Phase I Complexity: O(k * n)
     * IMPROVEMENT: O(k * n) -> O(k * log n)
     */
    public ArrayList<Order> getCustomerOrderHistory(int customerId) {
        Customer customer = searchCustomerById(customerId);
        if (customer == null) {
            return new ArrayList<>();
        }

        ArrayList<Order> customerOrders = new ArrayList<>();
        ArrayList<Integer> orderIds = customer.getOrderIds();
        
        for (int i = 0; i < orderIds.size(); i++) {
            Order order = orderService.searchOrderById(orderIds.get(i));
            if (order != null) {
                customerOrders.add(order);
            }
        }

        return customerOrders;
    }

    /**
     * Get customer reviews
     * Time Complexity: O(n * r) where n is products, r is reviews per product
     * Phase I Complexity: O(n * r) - same
     */
    public ArrayList<Review> getCustomerReviews(int customerId) {
        ArrayList<Review> customerReviews = new ArrayList<>();
        ArrayList<Product> allProducts = productService.getAllProducts();

        for (int i = 0; i < allProducts.size(); i++) {
            Product product = allProducts.get(i);
            ArrayList<Review> productReviews = product.getReviews();
            
            for (int j = 0; j < productReviews.size(); j++) {
                Review review = productReviews.get(j);
                if (review.getCustomerId() == customerId) {
                    customerReviews.add(review);
                }
            }
        }

        return customerReviews;
    }

    /**
     * PHASE II REQUIREMENT: Get all customers sorted alphabetically
     * Time Complexity: O(n) - inorder traversal of name BST
     * Phase I Complexity: O(n log n) - must sort ArrayList
     * IMPROVEMENT: O(n log n) -> O(n) - BST maintains sorted order
     */
    public ArrayList<Customer> getAllCustomersSorted() {
        return customerNameBST.inorderTraversal();
    }

    public ArrayList<Customer> getAllCustomers() {
        return customerIdBST.inorderTraversal();
    }

    public int getCustomerCount() {
        return customerIdBST.size();
    }
}