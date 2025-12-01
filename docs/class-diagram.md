# Class Diagram - E-Commerce Inventory & Order Management System

## UML Class Diagram

```
┌─────────────────────────────────────────────────────────────────────────┐
│                          CUSTOM DATA STRUCTURES                          │
└─────────────────────────────────────────────────────────────────────────┘

┌──────────────────────┐
│   ArrayList<T>       │
├──────────────────────┤
│ - elements: Object[] │
│ - size: int          │
├──────────────────────┤
│ + add(T): void       │
│ + get(int): T        │
│ + remove(int): T     │
│ + size(): int        │
│ + contains(T): bool  │
└──────────────────────┘

┌──────────────────────┐
│   LinkedList<T>      │
├──────────────────────┤
│ - head: Node<T>      │
│ - tail: Node<T>      │
│ - size: int          │
├──────────────────────┤
│ + addFirst(T): void  │
│ + addLast(T): void   │
│ + removeFirst(): T   │
│ + removeLast(): T    │
│ + get(int): T        │
└──────────────────────┘

┌──────────────────────┐
│      Stack<T>        │
├──────────────────────┤
│ - list: LinkedList<T>│
├──────────────────────┤
│ + push(T): void      │
│ + pop(): T           │
│ + peek(): T          │
│ + isEmpty(): bool    │
└──────────────────────┘

┌──────────────────────┐
│      Queue<T>        │
├──────────────────────┤
│ - list: LinkedList<T>│
├──────────────────────┤
│ + enqueue(T): void   │
│ + dequeue(): T       │
│ + peek(): T          │
│ + isEmpty(): bool    │
└──────────────────────┘

┌───────────────────────────┐
│  BinarySearchTree<T>      │
├───────────────────────────┤
│ - root: Node<T>           │
│ - size: int               │
├───────────────────────────┤
│ + insert(T): void         │
│ + search(T): T            │
│ + delete(T): void         │
│ + inorderTraversal(): void│
└───────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                            DOMAIN MODELS                                 │
└─────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────┐
│        Review              │
├────────────────────────────┤
│ - reviewId: int            │
│ - productId: int           │
│ - customerId: int          │
│ - rating: int              │
│ - comment: String          │
├────────────────────────────┤
│ + getRating(): int         │
│ + setRating(int): void     │
│ + getComment(): String     │
│ + setComment(String): void │
└────────────────────────────┘

┌──────────────────────────────────┐
│          Product                 │
├──────────────────────────────────┤
│ - productId: int                 │
│ - name: String                   │
│ - price: double                  │
│ - stock: int                     │
│ - reviews: ArrayList<Review>     │
├──────────────────────────────────┤
│ + addReview(Review): void        │
│ + removeReview(Review): void     │
│ + getAverageRating(): double     │
│ + isOutOfStock(): boolean        │
│ + decreaseStock(int): boolean    │
│ + increaseStock(int): void       │
└──────────────────────────────────┘
         │
         │ 1
         │
         │ *
         ▼
┌────────────────────────────┐
│        Review              │
└────────────────────────────┘

┌──────────────────────────────────┐
│          Customer                │
├──────────────────────────────────┤
│ - customerId: int                │
│ - name: String                   │
│ - email: String                  │
│ - orderIds: ArrayList<Integer>   │
├──────────────────────────────────┤
│ + addOrder(int): void            │
│ + removeOrder(int): void         │
└──────────────────────────────────┘
         │
         │ 1
         │
         │ *
         ▼
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
│ + addProduct(int): void              │
│ + removeProduct(int): void           │
│ + containsProduct(int): boolean      │
└──────────────────────────────────────┘

┌────────────────────────────┐
│      OrderStatus (enum)    │
├────────────────────────────┤
│ + PENDING                  │
│ + SHIPPED                  │
│ + DELIVERED                │
│ + CANCELED                 │
└────────────────────────────┘


┌─────────────────────────────────────────────────────────────────────────┐
│                              SERVICES                                    │
└─────────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│           ProductService                       │
├────────────────────────────────────────────────┤
│ - products: ArrayList<Product>                 │
│ - productIndex: BinarySearchTree<Product>      │
├────────────────────────────────────────────────┤
│ + addProduct(Product): void                    │
│ + removeProduct(int): boolean                  │
│ + updateProduct(int, ...): boolean             │
│ + searchById(int): Product                     │
│ + searchByName(String): ArrayList<Product>     │
│ + getOutOfStockProducts(): ArrayList<Product>  │
│ + addReviewToProduct(int, Review): boolean     │
│ + editReview(int, int, int, String): boolean   │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│           CustomerService                      │
├────────────────────────────────────────────────┤
│ - customers: ArrayList<Customer>               │
│ - productService: ProductService               │
│ - orderService: OrderService                   │
├────────────────────────────────────────────────┤
│ + registerCustomer(Customer): void             │
│ + searchCustomerById(int): Customer            │
│ + placeOrder(int, ArrayList<Integer>): Order   │
│ + getCustomerOrderHistory(int): ArrayList      │
│ + getCustomerReviews(int): ArrayList<Review>   │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│            OrderService                        │
├────────────────────────────────────────────────┤
│ - orders: ArrayList<Order>                     │
├────────────────────────────────────────────────┤
│ + createOrder(Order): void                     │
│ + cancelOrder(int): boolean                    │
│ + updateOrderStatus(int, OrderStatus): boolean │
│ + searchOrderById(int): Order                  │
│ + getOrdersBetweenDates(...): ArrayList<Order> │
│ + getOrdersByCustomer(int): ArrayList<Order>   │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│          AnalyticsService                      │
├────────────────────────────────────────────────┤
│ - productService: ProductService               │
├────────────────────────────────────────────────┤
│ + getTop3ProductsByRating(): ArrayList<Product>│
│ + getCommonHighRatedProducts(int, int): ...    │
│ + generateProductReport(Product): String       │
│ + generateInventoryReport(): String            │
└────────────────────────────────────────────────┘

┌────────────────────────────────────────────────┐
│             CSVReader                          │
├────────────────────────────────────────────────┤
│ + loadProducts(String, ProductService): void   │
│ + loadCustomers(String, CustomerService): void │
│ + loadOrders(String, OrderService): void       │
│ + loadReviews(String, ProductService): void    │
└────────────────────────────────────────────────┘
```

## Relationships

1. **Product ──< Review**: One-to-Many
   - A Product can have multiple Reviews
   - Each Review belongs to one Product

2. **Customer ──< Order**: One-to-Many
   - A Customer can place multiple Orders
   - Each Order belongs to one Customer

3. **Order ──< Product**: Many-to-Many (through productIds list)
   - An Order can contain multiple Products
   - A Product can be in multiple Orders

4. **Customer ──< Review**: One-to-Many
   - A Customer can write multiple Reviews
   - Each Review is written by one Customer

5. **Service Dependencies**:
   - CustomerService depends on ProductService and OrderService
   - AnalyticsService depends on ProductService
   - All services use custom data structures

## Key Design Patterns

1. **Service Layer Pattern**: Business logic separated into service classes
2. **Repository Pattern**: CSVReader handles data access
3. **Composite Pattern**: Data structures composed of simpler structures
4. **Strategy Pattern**: Different search strategies (by ID, by name)
