package com.ecommerce.models;

import com.ecommerce.datastructures.ArrayList;

/**
 * Customer entity with order history
 */
public class Customer implements Comparable<Customer> {
    private int customerId;
    private String name;
    private String email;
    private ArrayList<Integer> orderIds;

    public Customer(int customerId, String name, String email) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.orderIds = new ArrayList<>();
    }

    public void addOrder(int orderId) {
        orderIds.add(orderId);
    }

    public void removeOrder(int orderId) {
        for (int i = 0; i < orderIds.size(); i++) {
            if (orderIds.get(i) == orderId) {
                orderIds.remove(i);
                break;
            }
        }
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<Integer> getOrderIds() {
        return orderIds;
    }

    @Override
    public int compareTo(Customer other) {
        return Integer.compare(this.customerId, other.customerId);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", orderCount=" + orderIds.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId;
    }

    @Override
    public int hashCode() {
        return customerId;
    }
}
