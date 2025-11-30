# Phase II: Class Diagram

## Overview
This document presents the UML class diagrams for the E-Commerce System Phase II implementation using Binary Search Trees.

## Core Data Structure

```
┌─────────────────────────────────────────────────────────┐
│         BinarySearchTree<K, V>                          │
├─────────────────────────────────────────────────────────┤
│ - root: Node<K, V>                                      │
│ - size: int                                             │
├─────────────────────────────────────────────────────────┤
│ + insert(key: K, value: V): void                       │
│ + search(key: K): V                                     │
│ + delete(key: K): boolean                               │
│ + contains(key: K): boolean                             │
│ + update(key: K, newValue: V): boolean                  │
│ + rangeQuery(minKey: K, maxKey: K): ArrayList<V>       │
│ + inorderTraversal(): ArrayList<V>                      │
│ + getKeysSorted(): ArrayList<K>                         │
│ + size(): int                                           │
│ + isEmpty(): boolean                                    │
│ + clear(): void                                         │
│ + getMinKey(): K                                        │
│ + getMaxKey(): K                                        │
└─────────────────────────────────────────────────────────┘
                    │
                    │ contains
                    ▼
        ┌───────────────────────┐
        │    Node<K, V>         │
        ├───────────────────────┤
        │ + key: K              │
        │ + value: V            │
        │ + left: Node<K, V>    │
        │ + right: Node<K, V>   │
        └───────────────────────┘
```

## Service Layer Architecture

```
┌──────────────────────────────────────────────────────────────┐
│                    ProductService                            │
├──────────────────────────────────────────────────────────────┤
│ - productBST: BinarySearchTree<Integer, Product>            │
│ - priceIndex: BinarySearchTree<Double, ArrayList<Product>>  │
├──────────────────────────────────────────────────────────────┤
│ + addProduct(product: Product): void                         │
│ + removeProduct(productId: int): boolean                     │
│ + updateProduct(id: int, name: String, ...): boolean         │
│ + searchById(productId: int): Product                        │
│ + searchByName(name: String): ArrayList<Product>             │
│ + getProductsByPriceRange(min: double, max: double): ...    │
│ + getOutOfStockProducts(): ArrayList<Product>                │
│ + addReviewToProduct(productId: int, review: Review): bool   │
│ + getAllProducts(): ArrayList<Product>                       │
│ + getAllProductsSorted(): ArrayList<Product>                 │
│ + getProductCount(): int                                     │
└──────────────────────────────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌──────────────────────────────────────────────────────────────┐
│                    CustomerService                           │
├──────────────────────────────────────────────────────────────┤
│ - customerIdBST: BinarySearchTree<Integer, Customer>        │
│ - customerNameBST: BinarySearchTree<String, Customer>       │
│ - productService: ProductService                             │
│ - orderService: OrderService                                 │
├──────────────────────────────────────────────────────────────┤
│ + registerCustomer(customer: Customer): void                 │
│ + searchCustomerById(customerId: int): Customer              │
│ + searchCustomerByName(name: String): Customer               │
│ + placeOrder(customerId: int, productIds: ...): Order        │
│ + getCustomerOrderHistory(customerId: int): ...              │
│ + getCustomerReviews(customerId: int): ArrayList<Review>     │
│ + getAllCustomers(): ArrayList<Customer>                     │
│ + getAllCustomersSorted(): ArrayList<Customer>               │
│ + getCustomerCount(): int                                    │
└──────────────────────────────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌──────────────────────────────────────────────────────────────┐
│                    OrderService                              │
├──────────────────────────────────────────────────────────────┤
│ - orderIdBST: BinarySearchTree<Integer, Order>              │
│ - orderDateBST: BinarySearchTree<Long, ArrayList<Order>>    │
├──────────────────────────────────────────────────────────────┤
│ + createOrder(order: Order): void                            │
│ + cancelOrder(orderId: int): boolean                         │
│ + updateOrderStatus(orderId: int, status: ...): boolean      │
│ + searchOrderById(orderId: int): Order                       │
│ + getOrdersBetweenDates(start: LocalDate, ...): ...          │
│ + getOrdersByCustomer(customerId: int): ArrayList<Order>     │
│ + getOrdersByStatus(status: OrderStatus): ArrayList<Order>   │
│ + getAllOrders(): ArrayList<Order>                           │
│ + getOrderCount(): int                                       │
└──────────────────────────────────────────────────────────────┘
                    │
                    │ uses
                    ▼
┌──────────────────────────────────────────────────────────────┐
│                  AnalyticsService                            │
├──────────────────────────────────────────────────────────────┤
│ - productService: ProductService                             │
│ - customerService: CustomerService                           │
├──────────────────────────────────────────────────────────────┤
│ + getTop3ProductsByRating(): ArrayList<Product>              │
│ + getCommonHighRatedProducts(c1: int, c2: int): ...          │
│ + getCustomersWhoReviewedProduct(productId: int): ...        │
│ + generateProductReport(product: Product): String            │
│ + generateInventoryReport(): String                          │
└──────────────────────────────────────────────────────────────┘
```

## Model Classes

```
┌──────────────────────────────────────┐
│           Product                    │
├──────────────────────────────────────┤
│ - productId: int                     │
│ - name: String                       │
│ - price: double                      │
│ - stock: int                         │
│ - reviews: ArrayList<Review>         │
├──────────────────────────────────────┤
│ + compareTo(other: Product): int     │
│ + getAverageRating(): double         │
│ + isOutOfStock(): boolean            │
│ + addReview(review: Review): void    │
│ + decreaseStock(quantity: int): bool │
│ + increaseStock(quantity: int): void │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│           Customer                   │
├──────────────────────────────────────┤
│ - customerId: int                    │
│ - name: String                       │
│ - email: String                      │
│ - orderIds: ArrayList<Integer>       │
├──────────────────────────────────────┤
│ + addOrder(orderId: int): void       │
│ + removeOrder(orderId: int): void    │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│             Order                    │
├──────────────────────────────────────┤
│ - orderId: int                       │
│ - customerId: int                    │
│ - productIds: ArrayList<Integer>     │
│ - totalPrice: double                 │
│ - orderDate: LocalDate               │
│ - status: OrderStatus                │
├──────────────────────────────────────┤
│ + compareTo(other: Order): int       │
└──────────────────────────────────────┘

┌──────────────────────────────────────┐
│            Review                    │
├──────────────────────────────────────┤
│ - reviewId: int                      │
│ - productId: int                     │
│ - customerId: int                    │
│ - rating: int                        │
│ - comment: String                    │
└──────────────────────────────────────┘
```

## Key Relationships

1. **ProductService** uses **BinarySearchTree** for:
   - Product ID indexing (O(log n) search)
   - Price range indexing (O(log n + k) range queries)

2. **CustomerService** uses **BinarySearchTree** for:
   - Customer ID indexing (O(log n) search)
   - Customer name indexing (O(n) alphabetical sorting)

3. **OrderService** uses **BinarySearchTree** for:
   - Order ID indexing (O(log n) search)
   - Order date indexing (O(log n + k) date range queries)

4. **AnalyticsService** coordinates all services for complex queries

## Phase II Improvements

### Data Structure Changes
- **Phase I**: ArrayList (linear search O(n))
- **Phase II**: BinarySearchTree (logarithmic search O(log n))

### Key Benefits
1. Faster search operations
2. Efficient range queries
3. Automatic sorted traversals
4. Better scalability for large datasets