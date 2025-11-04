package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.Order;
import java.time.LocalDate;

/**
 * Service for managing orders
 * Time Complexity Analysis:
 * - createOrder(): O(1)
 * - cancelOrder(): O(n) linear search
 * - updateOrderStatus(): O(n) linear search + O(1) update
 * - searchOrderById(): O(n) linear search
 * - getOrdersBetweenDates(): O(n) iteration with date comparison
 */
public class OrderService {
    private ArrayList<Order> orders;

    public OrderService() {
        this.orders = new ArrayList<>();
    }

    public void createOrder(Order order) {
        orders.add(order);
    }

    public boolean cancelOrder(int orderId) {
        Order order = searchOrderById(orderId);
        if (order != null && order.getStatus() == Order.OrderStatus.PENDING) {
            order.setStatus(Order.OrderStatus.CANCELED);
            return true;
        }
        return false;
    }

    public boolean updateOrderStatus(int orderId, Order.OrderStatus newStatus) {
        Order order = searchOrderById(orderId);
        if (order != null) {
            order.setStatus(newStatus);
            return true;
        }
        return false;
    }

    public Order searchOrderById(int orderId) {
        for (int i = 0; i < orders.size(); i++) {
            Order o = orders.get(i);
            if (o.getOrderId() == orderId) {
                return o;
            }
        }
        return null;
    }

    public ArrayList<Order> getOrdersBetweenDates(LocalDate startDate, LocalDate endDate) {
        ArrayList<Order> result = new ArrayList<>();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            LocalDate orderDate = order.getOrderDate();
            
            if ((orderDate.isEqual(startDate) || orderDate.isAfter(startDate)) &&
                (orderDate.isEqual(endDate) || orderDate.isBefore(endDate))) {
                result.add(order);
            }
        }
        
        return result;
    }

    public ArrayList<Order> getOrdersByCustomer(int customerId) {
        ArrayList<Order> customerOrders = new ArrayList<>();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getCustomerId() == customerId) {
                customerOrders.add(order);
            }
        }
        
        return customerOrders;
    }

    public ArrayList<Order> getOrdersByStatus(Order.OrderStatus status) {
        ArrayList<Order> statusOrders = new ArrayList<>();
        
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            if (order.getStatus() == status) {
                statusOrders.add(order);
            }
        }
        
        return statusOrders;
    }

    public ArrayList<Order> getAllOrders() {
        return orders;
    }

    public int getOrderCount() {
        return orders.size();
    }
}
