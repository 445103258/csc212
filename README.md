# E-Commerce Inventory & Order Management System

A comprehensive inventory and order management system built with custom data structures, Java core logic, Python FastAPI backend, and React TypeScript frontend.

## Project Overview

This system helps businesses manage product inventory, process customer orders, and gain insights through analytics. Built entirely with custom data structures (no Java Collections Framework) as a CSC 212 course project.

## Features

### Core Functionality
-  **Product Management**: Add, update, remove, search products by ID or name
-  **Inventory Tracking**: Monitor stock levels and out-of-stock items
-  **Customer Management**: Register customers and track order history
-  **Order Processing**: Place orders with automatic inventory validation
-  **Review System**: Add/edit reviews with 1-5 star ratings
-  **Analytics**: Top products by rating, common preferences, inventory reports

### Technical Highlights
-  **Custom Data Structures**: ArrayList, LinkedList, Stack, Queue, Binary Search Tree
-  **Layered Architecture**: Separation of concerns with repository, service, and view layers
-  **RESTful API**: FastAPI backend with Pydantic validation
-  **CSV Integration**: Import/export data from CSV files
-  **Complexity Analysis**: Comprehensive time and space complexity documentation

## Architecture

```
┌─────────────────────────────────────┐
│   Frontend (React + TypeScript)     │
│         shadcn-ui + Tailwind        │
└─────────────────────────────────────┘
                 ↕ HTTP/REST
┌─────────────────────────────────────┐
│   Backend API (Python + FastAPI)    │
│  Repository → Service → View Layers │
└─────────────────────────────────────┘
                 ↕ Data Access
┌─────────────────────────────────────┐
│   Core Logic (Java)                 │
│   Custom Data Structures + Services │
└─────────────────────────────────────┘
                 ↕ File I/O
┌─────────────────────────────────────┐
│        Data Layer (CSV Files)       │
└─────────────────────────────────────┘
```

##  Project Structure

```
ecommerce-system/
├── java-core/                      # Core business logic
│   └── src/com/ecommerce/
│       ├── datastructures/        # Custom data structures implementations
│       │   ├── ArrayList.java
│       │   ├── LinkedList.java
│       │   ├── Stack.java
│       │   ├── Queue.java
│       │   └── BinarySearchTree.java
│       ├── models/                # Domain models
│       │   ├── Product.java
│       │   ├── Customer.java
│       │   ├── Order.java
│       │   └── Review.java
│       ├── services/              # Business services
│       │   ├── ProductService.java
│       │   ├── CustomerService.java
│       │   ├── OrderService.java
│       │   └── AnalyticsService.java
│       ├── utils/                 # Utilities
│       │   └── CSVReader.java
│       └── Main.java              # Entry point (Java TUI)
├── python-api/                     # Backend API
│   ├── data/                      # CSV data files
│   │   ├── products.csv
│   │   ├── customers.csv
│   │   ├── orders.csv
│   │   └── reviews.csv
│   ├── app/
│   │   ├── repositories/          # Data access layer
│   │   │   └── csv_repository.py
│   │   ├── models/                # Pydantic models
│   │   │   └── entities.py
│   │   ├── services/              # Business logic
│   │   │   └── ecommerce_service.py
│   │   ├── views/                 # API endpoints
│   │   │   └── api_routes.py
│   │   └── main.py                # FastAPI app
│   └── requirements.txt
├── docs/                          # Documentation
│   ├── todo.md
│   ├── class-diagram.md
│   ├── complexity-analysis.md
│   └── report.md
└── README.md
```

## Getting Started

### Prerequisites
- Java JDK 11 or higher
- Python 3.8 or higher
- Node.js 16 or higher
- pnpm (for frontend)

### Installation & Running

#### 1. Java Core System
```bash
cd ecommerce-system/java-core

# Compile
javac -d bin src/com/ecommerce/**/*.java

# Run
java -cp bin com.ecommerce.Main
```

#### 2. Python API Backend
```bash
cd ecommerce-system/python-api

# Install dependencies
pip install -r requirements.txt

# Run server
uvicorn app.main:app --reload --port 8000
```

Access API documentation at: `http://localhost:8000/docs`

#### 3. Frontend Dashboard
```bash
cd shadcn-ui

# Install dependencies
pnpm install

# Run development server
pnpm run dev
```

### Using docker-compose
```bash
# Build images and run services
docker compose up --build
```

## API Endpoints

### Products
- `GET /api/v1/products` - Get all products
- `GET /api/v1/products/{id}` - Get product by ID
- `POST /api/v1/products` - Create new product
- `PUT /api/v1/products/{id}` - Update product
- `DELETE /api/v1/products/{id}` - Delete product
- `GET /api/v1/products/stock/out-of-stock` - Get out-of-stock products

### Customers
- `GET /api/v1/customers` - Get all customers
- `GET /api/v1/customers/{id}` - Get customer by ID
- `POST /api/v1/customers` - Register new customer
- `GET /api/v1/customers/{id}/orders` - Get customer orders
- `GET /api/v1/customers/{id}/reviews` - Get customer reviews

### Orders
- `GET /api/v1/orders` - Get all orders
- `GET /api/v1/orders?start_date=...&end_date=...` - Get orders between dates
- `GET /api/v1/orders/{id}` - Get order by ID
- `POST /api/v1/orders` - Create new order
- `PATCH /api/v1/orders/{id}/status` - Update order status
- `POST /api/v1/orders/{id}/cancel` - Cancel order

### Reviews
- `POST /api/v1/reviews` - Create new review

### Analytics
- `GET /api/v1/analytics/top-products?limit=3` - Get top products by rating
- `GET /api/v1/analytics/common-products?customer_id1=...&customer_id2=...` - Get common high-rated products

### Health
- `GET /api/v1/health` - Health check and system stats

## Complexity Analysis

### Data Structures
| Structure | Operation | Time | Space |
|-----------|-----------|------|-------|
| ArrayList | add() | O(1) amortized | O(n) |
| ArrayList | get() | O(1) | O(1) |
| LinkedList | addFirst/Last() | O(1) | O(n) |
| Stack/Queue | push/pop | O(1) | O(n) |
| BST | search() | O(log n) avg | O(n) |

### Business Operations
| Operation | Time Complexity | Notes |
|-----------|----------------|-------|
| Add Product | O(log n) | BST insertion |
| Search by ID | O(n) | Linear search |
| Place Order | O(n*p) | Validate p products |
| Top 3 Products | O(n²) | Bubble sort |
| Customer Reviews | O(n*r) | Check all products |

See [docs/complexity-analysis.md](docs/complexity-analysis.md) for detailed analysis.

## Documentation

- **[Class Diagram](docs/class-diagram.md)**: UML diagrams and relationships
- **[Complexity Analysis](docs/complexity-analysis.md)**: Time and space complexity
- **[Project Report](docs/report.md)**: Complete project documentation

## Technology Stack

### Backend
- **Java 11+**: Core business logic
- **Python 3.8+**: API backend
- **FastAPI**: Web framework
- **Pydantic**: Data validation
- **Uvicorn**: ASGI server

### Frontend
- **React 18**: UI library
- **TypeScript**: Type safety
- **shadcn-ui**: Component library
- **Tailwind CSS**: Styling
- **Vite**: Build tool
