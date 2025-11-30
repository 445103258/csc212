package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.datastructures.BinarySearchTree;
import com.ecommerce.models.Order;
import java.time.LocalDate;

/**
 * PHASE II: Order Service using Binary Search Tree
 * 
 * Time Complexity Analysis (Phase II with BST):
 * - createOrder(): O(log n) - BST insertion
 * - cancelOrder(): O(log n) - BST search + O(1) update
 * - updateOrderStatus(): O(log n) - BST search + O(1) update
 * - searchOrderById(): O(log n) - BST search
 * - getOrdersBetweenDates(): O(log n + k) - BST range query, k = results
 * - getOrdersByCustomer(): O(n) - must check all orders
 * 
 * Comparison with Phase I:
 * Phase I - ArrayList:
 *   - createOrder(): O(1) amortized
 *   - searchOrderById(): O(n) linear search
 *   - cancelOrder(): O(n) search + O(1) update
 *   - getOrdersBetweenDates(): O(n) - check all orders
 * 
 * Phase II - BST:
 *   - createOrder(): O(log n)
 *   - searchOrderById(): O(log n) - IMPROVED from O(n)
 *   - cancelOrder(): O(log n) - IMPROVED from O(n)
 *   - getOrdersBetweenDates(): O(log n + k) - IMPROVED for small result sets
 */
public class OrderService {
    // Phase II: BST for O(log n) operations
    private BinarySearchTree<Integer, Order> orderIdBST;  // Key: orderId
    private BinarySearchTree<Long, ArrayList<Order>> orderDateBST;  // Key: date as epoch day
    
    public OrderService() {
        this.orderIdBST = new BinarySearchTree<>();
        this.orderDateBST = new BinarySearchTree<>();
    }

    /**
     * Create a new order
     * Time Complexity: O(log n) - two BST insertions
     * Phase I Complexity: O(1) - ArrayList add
     */
    public void createOrder(Order order) {
        orderIdBST.insert(order.getOrderId(), order);
        
        // Index by date for range queries
        long epochDay = order.getOrderDate().toEpochDay();
        ArrayList<Order> ordersOnDate = orderDateBST.search(epochDay);
        if (ordersOnDate == null) {
            ordersOnDate = new ArrayList<>();
        }
        ordersOnDate.add(order);
        orderDateBST.insert(epochDay, ordersOnDate);
    }

    /**
     * Cancel an order
     * Time Complexity: O(log n) - BST search + O(1) update
     * Phase I Complexity: O(n) - linear search + O(1) update
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public boolean cancelOrder(int orderId) {
        Order order = searchOrderById(orderId);
        if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CANCELED);
            return true;
        }
        return false;
    }

    /**
     * Update order status
     * Time Complexity: O(log n) - BST search + O(1) update
     * Phase I Complexity: O(n) - linear search + O(1) update
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        Order order = searchOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            return true;
        }
        return false;
    }

    /**
     * Search order by ID using BST
     * Time Complexity: O(log n) - BST search
     * Phase I Complexity: O(n) - linear search
     * IMPROVEMENT: O(n) -> O(log n)
     */
    public Order searchOrderById(int orderId) {
        return orderIdBST.search(orderId);
    }

    /**
     * PHASE II REQUIREMENT: Get orders between two dates using BST range query
     * Time Complexity: O(log n + k) where k is number of results
     * Phase I Complexity: O(n) - must check all orders
     * IMPROVEMENT: More efficient for small date ranges
     */
    public ArrayList<Order> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        ArrayList<Order> result = new ArrayList<>();
        
        long startEpoch = startDate.toEpochDay();
        long endEpoch = endDate.toEpochDay();
        
        // Use BST range query for efficient date filtering
        ArrayList<ArrayList<Order>> dateGroups = orderDateBST.rangeQuery(startEpoch, endEpoch);
        
        for (int i = 0; i < dateGroups.size(); i++) {
            ArrayList<Order> group = dateGroups.get(i);
            for (int j = 0; j < group.size(); j++) {
                result.add(group.get(j));
            }
        }
        
        return result;
    }

    /**
     * Get orders by customer
     * Time Complexity: O(n) - must check all orders
     * Phase I Complexity: O(n) - same
     */
    public ArrayList<Order> getOrdersByCustomer(int customerId) {
        ArrayList<Order> customerOrders = new ArrayList<>();
        ArrayList<Order> allOrders = orderIdBST.inorderTraversal();
        
        for (int i = 0; i < allOrders.size(); i++) {
            Order order = allOrders.get(i);
            if (order.getCustomerId() == customerId) {
                customerOrders.add(order);
            }
        }
        
        return customerOrders;
    }

    /**
     * Get orders by status
     * Time Complexity: O(n) - must check all orders
     * Phase I Complexity: O(n) - same
     */
    public ArrayList<Order> getOrdersByStatus(Order.OrderStatus status) {
        ArrayList<Order> statusOrders = new ArrayList<>();
        ArrayList<Order> allOrders = orderIdBST.inorderTraversal();
        
        for (int i = 0; i < allOrders.size(); i++) {
            Order order = allOrders.get(i);
            if (order.getStatus() == status) {
                statusOrders.add(order);
            }
        }
        
        return statusOrders;
    }

    /**
     * Get all orders sorted by ID
     * Time Complexity: O(n) - inorder traversal
     */
    public ArrayList<Order> getAllOrders() {
        return orderIdBST.inorderTraversal();
    }

    public int getOrderCount() {
        return orderIdBST.size();
    }
}