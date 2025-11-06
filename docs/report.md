# E-Commerce Inventory & Order Management System
## Project Report

**Course**: CSC 212  
**Semester**: Fall 2025  
**Date**: November 6, 2025

---

## Table of Contents
1. [Executive Summary](#executive-summary)
2. [System Architecture](#system-architecture)
3. [Implementation Details](#implementation-details)
4. [Complexity Analysis](#complexity-analysis)
5. [Testing and Validation](#testing-and-validation)
6. [Conclusion](#conclusion)

---

## Executive Summary

This project implements a comprehensive E-Commerce Inventory & Order Management System designed to help businesses manage product inventory, process customer orders, and gain insights through analytics. The system is built entirely with custom data structures (no Java Collections) and follows a layered architecture pattern.

### Key Features
-  Complete custom data structure implementations (ArrayList, LinkedList, Stack, Queue, BST)
-  Product inventory management with stock tracking
-  Customer registration and order placement
-  Order management with status tracking
-  Review system with rating aggregation
-  Analytics for top products and common preferences
-  CSV data import/export
-  RESTful API backend with Python/FastAPI
-  Comprehensive time and space complexity analysis

---

## System Architecture

### Three-Tier Architecture

```
┌─────────────────────────────────────────────────────────┐
│                  Presentation Layer                      │
│              (TypeScript React Frontend)                 │
└─────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                   Application Layer                      │
│                (Python FastAPI Backend)                  │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │  Views/API   │→ │   Services   │→ │ Repositories │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                    Business Logic Layer                  │
│                   (Java Core System)                     │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Services   │→ │    Models    │→ │ Data Structs │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
                           ↕
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│                    (CSV Files)                           │
└─────────────────────────────────────────────────────────┘
```

---

## Implementation Details

### Custom Data Structures

All data structures were implemented from scratch without using Java Collections Framework.

#### 1. ArrayList<T>
Dynamic array with automatic resizing capability.

**Time Complexity**:
- add(): O(1) amortized, O(n) worst case
- get(): O(1)
- remove(): O(n)

**Space Complexity**: O(n)

#### 2. LinkedList<T>
Doubly-linked list with head and tail pointers.

**Time Complexity**:
- addFirst/addLast(): O(1)
- removeFirst/removeLast(): O(1)
- get(): O(n)

**Space Complexity**: O(n)

#### 3. Stack<T>
LIFO data structure using LinkedList as backing store.

**Time Complexity**: All operations O(1)

#### 4. Queue<T>
FIFO data structure using LinkedList as backing store.

**Time Complexity**: All operations O(1)

#### 5. BinarySearchTree<T>
BST for efficient searching and ordering.

**Time Complexity**:
- insert/search/delete(): O(log n) average, O(n) worst

**Space Complexity**: O(n)

### Domain Models

#### Product
- Attributes: productId, name, price, stock, reviews list
- Methods: addReview(), getAverageRating(), isOutOfStock(), decreaseStock()

#### Customer
- Attributes: customerId, name, email, orderIds list
- Methods: addOrder(), removeOrder()

#### Order
- Attributes: orderId, customerId, productIds, totalPrice, orderDate, status
- Methods: addProduct(), removeProduct(), containsProduct()
- Status enum: PENDING, SHIPPED, DELIVERED, CANCELLED

#### Review
- Attributes: reviewId, productId, customerId, rating (1-5), comment
- Validation: Rating must be between 1 and 5

### Service Layer

#### ProductService
Manages product inventory and operations.

**Key Operations**:
1. Add/Remove/Update products
2. Search by ID (O(n)) or name (O(n))
3. Track out-of-stock products (O(n))
4. Add/edit reviews to products

#### CustomerService
Handles customer management and order placement.

**Key Operations**:
1. Register new customers
2. Place orders with inventory validation
3. View order history
4. Extract customer reviews

#### OrderService
Manages order lifecycle.

**Key Operations**:
1. Create/cancel orders
2. Update order status
3. Search orders by ID
4. Get orders between dates

#### AnalyticsService
Provides business intelligence.

**Key Operations**:
1. Top 3 products by rating (O(n²) with bubble sort)
2. Common high-rated products between customers (O(n*r))
3. Generate inventory reports

### Python Backend API

#### Repository Layer
- CSVRepository: Handles all CSV file I/O
- Methods for reading/writing products, customers, orders, reviews
- Parses complex CSV formats (semicolon-separated product IDs)

#### Service Layer
- ECommerceService: Implements business logic
- Links reviews to products and orders to customers
- Handles data transformation between CSV and Python models
- Validates business rules (stock availability, customer existence)

#### View Layer
- RESTful API endpoints using FastAPI
- Pydantic models for request/response validation
- CORS enabled for frontend integration

**API Endpoints**:
- GET/POST/PUT/DELETE `/api/v1/products`
- GET/POST `/api/v1/customers`
- GET/POST `/api/v1/orders`
- POST `/api/v1/reviews`
- GET `/api/v1/analytics/top-products`
- GET `/api/v1/analytics/common-products`

---

## Complexity Analysis

### Data Structure Operations

| Data Structure | Operation | Time Complexity | Space Complexity |
|---------------|-----------|----------------|------------------|
| ArrayList | add() | O(1) amortized | O(n) |
| ArrayList | get() | O(1) | O(1) |
| ArrayList | remove() | O(n) | O(1) |
| LinkedList | addFirst/Last() | O(1) | O(n) |
| LinkedList | get() | O(n) | O(1) |
| Stack | push/pop() | O(1) | O(n) |
| Queue | enqueue/dequeue() | O(1) | O(n) |
| BST | insert/search() | O(log n) avg | O(n) |

### Business Operations

| Service | Operation | Time Complexity | Explanation |
|---------|-----------|----------------|-------------|
| ProductService | addProduct() | O(log n) | BST insertion |
| ProductService | searchById() | O(n) | Linear search |
| ProductService | searchByName() | O(n) | Linear search with substring |
| CustomerService | placeOrder() | O(n*p) | Validate p products |
| CustomerService | getCustomerReviews() | O(n*r) | Check all products and reviews |
| OrderService | getOrdersBetweenDates() | O(n) | Linear scan with date filter |
| AnalyticsService | getTop3Products() | O(n²) | Bubble sort |
| AnalyticsService | getCommonProducts() | O(n*r) | Check all reviews |

### Overall System Complexity

**Space Complexity**: O(P + C + O + R)
- P = products
- C = customers
- O = orders
- R = reviews

**Critical Operations**:
1. Most frequent: searchById O(n), getCustomerReviews O(n*r)
2. Most expensive: getTop3Products O(n²)

### Optimization Opportunities

1. **Product Search**: Use BST exclusively for O(log n) ID searches
2. **Top Products**: Implement heap or quickselect for O(n log k) or O(n)
3. **Customer Reviews**: Maintain reverse index for O(1) lookup
4. **Date Queries**: Use BST sorted by date for O(log n + k) results

---

## Testing and Validation

### Test Data
The system includes sample CSV data:
- 5 products (including out-of-stock items)
- 4 customers
- 4 orders with various statuses
- 7 reviews with ratings 4-5

### Edge Cases Handled

1. **Out of Stock**: Order placement fails if product unavailable
2. **Invalid Customer**: Order fails if customer doesn't exist
3. **Rating Validation**: Reviews must have rating 1-5
4. **Empty Results**: Graceful handling of no results
5. **CSV Parsing**: Handles quoted strings and semicolon separators

---

## Conclusion

### Achievements

1. **Complete Custom Implementation**: All data structures built from scratch
2. **Comprehensive Functionality**: All project requirements met
3. **Layered Architecture**: Clean separation of concerns
4. **Full Documentation**: Class diagrams, complexity analysis, and reports

---

## Appendix

### How to Run

#### Java Core System
```bash
cd java-core/
javac -d bin src/com/ecommerce/**/*.java
java -cp bin com.ecommerce.Main
```

#### Python API Backend
```bash
cd python-api/
pip install -r requirements.txt
uvicorn app.main:app --reload --port 8000
```

#### Frontend
```bash
cd shadcn-ui/
pnpm install
pnpm run dev
```

### API Documentation
Access interactive API docs at: `http://localhost:8000/docs`

### Project Repository Structure
```
ecommerce-system/
├── java-core/              # Core business logic
│   └── src/com/ecommerce/
│       ├── datastructures/ # Custom implementations
│       ├── models/         # Domain models
│       ├── services/       # Business services
│       ├── utils/          # Utilities
│       └── Main.java       # Entry point
├── python-api/             # Backend API
│   ├── data/              # CSV files
│   └── app/
│       ├── repositories/  # Data access
│       ├── models/        # Pydantic models
│       ├── services/      # Business logic
│       ├── views/         # API endpoints
│       └── main.py        # FastAPI app
└── docs/                  # Documentation
    ├── class-diagram.md
    ├── complexity-analysis.md
    └── report.md
```

---

**End of Report**
