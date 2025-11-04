package com.ecommerce.services;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.Customer;
import com.ecommerce.models.Order;
import com.ecommerce.models.Product;
import com.ecommerce.models.Review;

/**
 * Service for managing customers and their interactions
 * Time Complexity Analysis:
 * - registerCustomer(): O(1)
 * - searchCustomerById(): O(n) linear search
 * - placeOrder(): O(n) for product validation + O(1) order creation
 * - getCustomerOrderHistory(): O(m) where m is number of orders
 * - getCustomerReviews(): O(n*r) where n is products, r is reviews per product
 */
public class CustomerService {
    private ArrayList<Customer> customers;
    private ProductService productService;
    private OrderService orderService;

    public CustomerService(ProductService productService, OrderService orderService) {
        this.customers = new ArrayList<>();
        this.productService = productService;
        this.orderService = orderService;
    }

    public void registerCustomer(Customer customer) {
        customers.add(customer);
    }

    public Customer searchCustomerById(int customerId) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            if (c.getCustomerId() == customerId) {
                return c;
            }
        }
        return null;
    }

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

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public int getCustomerCount() {
        return customers.size();
    }
}
