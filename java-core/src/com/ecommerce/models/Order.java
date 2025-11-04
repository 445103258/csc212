package com.ecommerce.models;

import com.ecommerce.datastructures.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Order entity representing customer purchases
 */
public class Order implements Comparable<Order> {
    private int orderId;
    private int customerId;
    private ArrayList<Integer> productIds;
    private double totalPrice;
    private LocalDate orderDate;
    private OrderStatus status;

    public enum OrderStatus {
        PENDING("Pending"),
        SHIPPED("Shipped"),
        DELIVERED("Delivered"),
        CANCELED("Canceled");

        private final String displayName;

        OrderStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static OrderStatus fromString(String status) {
            for (OrderStatus s : OrderStatus.values()) {
                if (s.displayName.equalsIgnoreCase(status) || s.name().equalsIgnoreCase(status)) {
                    return s;
                }
            }
            return PENDING;
        }
    }

    public Order(int orderId, int customerId, ArrayList<Integer> productIds, double totalPrice, 
                 LocalDate orderDate, OrderStatus status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productIds = productIds;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
    }

    public Order(int orderId, int customerId, ArrayList<Integer> productIds, double totalPrice, 
                 String orderDateStr, String statusStr) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.productIds = productIds;
        this.totalPrice = totalPrice;
        this.orderDate = LocalDate.parse(orderDateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        this.status = OrderStatus.fromString(statusStr);
    }

    public void addProduct(int productId) {
        productIds.add(productId);
    }

    public void removeProduct(int productId) {
        for (int i = 0; i < productIds.size(); i++) {
            if (productIds.get(i) == productId) {
                productIds.remove(i);
                break;
            }
        }
    }

    public boolean containsProduct(int productId) {
        for (int i = 0; i < productIds.size(); i++) {
            if (productIds.get(i) == productId) {
                return true;
            }
        }
        return false;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public ArrayList<Integer> getProductIds() {
        return productIds;
    }

    public void setProductIds(ArrayList<Integer> productIds) {
        this.productIds = productIds;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    @Override
    public int compareTo(Order other) {
        return this.orderDate.compareTo(other.orderDate);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", productCount=" + productIds.size() +
                ", totalPrice=" + totalPrice +
                ", orderDate=" + orderDate +
                ", status=" + status.getDisplayName() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId;
    }

    @Override
    public int hashCode() {
        return orderId;
    }
}
