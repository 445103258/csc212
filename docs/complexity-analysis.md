# Time and Space Complexity Analysis

## Custom Data Structures

### ArrayList<T>
| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| add(T) | O(1) amortized, O(n) worst | O(1) | Worst case when array needs resizing |
| get(int) | O(1) | O(1) | Direct index access |
| set(int, T) | O(1) | O(1) | Direct index access |
| remove(int) | O(n) | O(1) | Requires shifting elements |
| remove(T) | O(n) | O(1) | Search + remove |
| contains(T) | O(n) | O(1) | Linear search |
| size() | O(1) | O(1) | Stored as field |
| **Overall Space** | - | O(n) | n = number of elements |

### LinkedList<T>
| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| addFirst(T) | O(1) | O(1) | Direct head insertion |
| addLast(T) | O(1) | O(1) | Direct tail insertion |
| removeFirst() | O(1) | O(1) | Direct head removal |
| removeLast() | O(1) | O(1) | Direct tail removal |
| get(int) | O(n) | O(1) | Must traverse from head |
| remove(T) | O(n) | O(1) | Linear search required |
| contains(T) | O(n) | O(1) | Linear search |
| size() | O(1) | O(1) | Stored as field |
| **Overall Space** | - | O(n) | n nodes + pointers |

### Stack<T>
| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| push(T) | O(1) | O(1) | Uses LinkedList.addFirst |
| pop() | O(1) | O(1) | Uses LinkedList.removeFirst |
| peek() | O(1) | O(1) | Uses LinkedList.getFirst |
| isEmpty() | O(1) | O(1) | Checks LinkedList.isEmpty |
| **Overall Space** | - | O(n) | Wrapper around LinkedList |

### Queue<T>
| Operation | Time Complexity | Space Complexity | Notes |
|-----------|----------------|------------------|-------|
| enqueue(T) | O(1) | O(1) | Uses LinkedList.addLast |
| dequeue() | O(1) | O(1) | Uses LinkedList.removeFirst |
| peek() | O(1) | O(1) | Uses LinkedList.getFirst |
| isEmpty() | O(1) | O(1) | Checks LinkedList.isEmpty |
| **Overall Space** | - | O(n) | Wrapper around LinkedList |

### BinarySearchTree<T>
| Operation | Time Complexity (Avg) | Time Complexity (Worst) | Space Complexity | Notes |
|-----------|--------------------|----------------------|------------------|-------|
| insert(T) | O(log n) | O(n) | O(1) | Worst case: skewed tree |
| search(T) | O(log n) | O(n) | O(1) | Worst case: skewed tree |
| delete(T) | O(log n) | O(n) | O(1) | Worst case: skewed tree |
| inorderTraversal() | O(n) | O(n) | O(n) | Visits all nodes |
| **Overall Space** | - | - | O(n) | n nodes |

## Business Operations

### ProductService
| Operation | Time Complexity | Explanation |
|-----------|----------------|-------------|
| addProduct() | O(log n) | BST insertion for indexing |
| removeProduct() | O(n) + O(log n) | Linear search + BST deletion |
| updateProduct() | O(n) | Linear search for product |
| searchById() | O(n) | Linear search through ArrayList |
| searchByName() | O(n) | Linear search with substring matching |
| getOutOfStockProducts() | O(n) | Iterate through all products |
| addReviewToProduct() | O(n) + O(1) | Search product + add review |
| editReview() | O(n) + O(r) | Search product + search review |

**Note**: searchById could be O(log n) if we used BST exclusively, but we maintain ArrayList for ordering.

### CustomerService
| Operation | Time Complexity | Explanation |
|-----------|----------------|-------------|
| registerCustomer() | O(1) | Add to ArrayList |
| searchCustomerById() | O(n) | Linear search |
| placeOrder() | O(n*p) | Validate each product in order |
| getCustomerOrderHistory() | O(m) | m = number of orders for customer |
| getCustomerReviews() | O(n*r) | n products * r reviews per product |

### OrderService
| Operation | Time Complexity | Explanation |
|-----------|----------------|-------------|
| createOrder() | O(1) | Add to ArrayList |
| cancelOrder() | O(n) | Search order + update status |
| updateOrderStatus() | O(n) | Linear search for order |
| searchOrderById() | O(n) | Linear search |
| getOrdersBetweenDates() | O(n) | Iterate and filter by date |
| getOrdersByCustomer() | O(n) | Iterate and filter by customer |
| getOrdersByStatus() | O(n) | Iterate and filter by status |

### AnalyticsService
| Operation | Time Complexity | Explanation |
|-----------|----------------|-------------|
| getTop3ProductsByRating() | O(n²) | Bubble sort on products |
| getCommonHighRatedProducts() | O(n*r) | Check all products and their reviews |
| generateProductReport() | O(r) | Iterate through product reviews |
| generateInventoryReport() | O(n) | Iterate through all products |

**Note**: getTop3ProductsByRating uses bubble sort (O(n²)). Could be optimized to O(n log n) with merge sort or O(n) with selection algorithm for top k.

## Overall System Complexity

### Space Complexity
Total space: **O(P + C + O + R)**
- P = number of products
- C = number of customers
- O = number of orders
- R = number of reviews

Each entity is stored once in memory with references maintained through IDs.

### Critical Path Analysis

**Most Frequent Operations** (Expected to be called often):
1. searchById: O(n) - Could be optimized with better indexing
2. getCustomerReviews: O(n*r) - Efficient for small datasets
3. getOrdersBetweenDates: O(n) - Linear scan acceptable

**Most Expensive Operations**:
1. getTop3ProductsByRating: O(n²) - Due to bubble sort
2. getCustomerReviews: O(n*r) - Depends on data size
3. getCommonHighRatedProducts: O(n*r) - Multiple iterations

## Optimization Opportunities

1. **Product Search**: Maintain a separate BST index for O(log n) ID searches
2. **Top Products**: Use heap or quickselect for O(n log k) or O(n) complexity
3. **Customer Reviews**: Maintain reverse index from customer to reviews
4. **Date Range Queries**: Use BST sorted by date for O(log n + k) where k = results

## Comparison with Java Collections

| Operation | Custom Implementation | Java Collections |
|-----------|---------------------|------------------|
| ArrayList.add() | O(1) amortized | O(1) amortized |
| LinkedList.add() | O(1) | O(1) |
| BST.search() | O(log n) avg | HashMap O(1) avg |
| Sort | O(n²) bubble | O(n log n) TimSort |

Our custom implementations match Java Collections for basic operations but lack advanced optimizations like HashMap's O(1) lookup and efficient sorting algorithms.
