from fastapi import APIRouter, HTTPException, Query
from typing import List, Optional
from datetime import date
from pydantic import BaseModel
from app.models.entities import (
    Product, Customer, Order, Review,
    ProductCreate, CustomerCreate, OrderCreate, ReviewCreate,
    OrderStatus
)
from app.services.ecommerce_service import ECommerceService
from app.repositories.csv_repository import CSVRepository

router = APIRouter()

# Initialize repository and service
repository = CSVRepository(data_dir="/app/data")
service = ECommerceService(repository)


class OrderStatusUpdate(BaseModel):
    status: str


@router.get("/products", response_model=List[Product])
async def get_products(
    name: Optional[str] = None,
    min_price: Optional[float] = None,
    max_price: Optional[float] = None
):
    """Get all products or search by name/price range"""
    if name:
        return service.search_products_by_name(name)
    elif min_price is not None and max_price is not None:
        return service.get_products_by_price_range(min_price, max_price)
    else:
        return service.get_all_products()


@router.get("/products/{product_id}", response_model=Product)
async def get_product(product_id: int):
    """Get product by ID"""
    product = service.get_product_by_id(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product


@router.post("/products", response_model=Product)
async def create_product(product: ProductCreate):
    """Create a new product"""
    return service.add_product(product)


@router.put("/products/{product_id}", response_model=Product)
async def update_product(product_id: int, product: ProductCreate):
    """Update product"""
    updated = service.update_product(product_id, product)
    if not updated:
        raise HTTPException(status_code=404, detail="Product not found")
    return updated


@router.delete("/products/{product_id}")
async def delete_product(product_id: int):
    """Delete product"""
    if not service.delete_product(product_id):
        raise HTTPException(status_code=404, detail="Product not found")
    return {"message": "Product deleted successfully"}


@router.get("/products/stock/out-of-stock", response_model=List[Product])
async def get_out_of_stock_products():
    """Get out-of-stock products"""
    return service.get_out_of_stock_products()


@router.get("/customers", response_model=List[Customer])
async def get_customers(sorted: bool = Query(False, description="Return customers sorted alphabetically")):
    """Get all customers"""
    if sorted:
        return service.get_all_customers_sorted()
    else:
        return service.get_all_customers()


@router.get("/customers/{customer_id}", response_model=Customer)
async def get_customer(customer_id: int):
    """Get customer by ID"""
    customer = service.get_customer_by_id(customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")
    return customer


@router.post("/customers", response_model=Customer)
async def create_customer(customer: CustomerCreate):
    """Create a new customer"""
    return service.register_customer(customer)


@router.put("/customers/{customer_id}", response_model=Customer)
async def update_customer(customer_id: int, customer: CustomerCreate):
    """Update customer"""
    updated = service.update_customer(customer_id, customer)
    if not updated:
        raise HTTPException(status_code=404, detail="Customer not found")
    return updated


@router.delete("/customers/{customer_id}")
async def delete_customer(customer_id: int):
    """Delete customer"""
    if not service.delete_customer(customer_id):
        raise HTTPException(status_code=404, detail="Customer not found")
    return {"message": "Customer deleted successfully"}


@router.get("/customers/{customer_id}/orders", response_model=List[Order])
async def get_customer_orders(customer_id: int):
    """Get customer order history"""
    customer = service.get_customer_by_id(customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")
    return service.get_customer_order_history(customer_id)


@router.get("/customers/{customer_id}/reviews", response_model=List[Review])
async def get_customer_reviews(customer_id: int):
    """Get customer reviews"""
    customer = service.get_customer_by_id(customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")
    return service.get_customer_reviews(customer_id)


@router.get("/orders", response_model=List[Order])
async def get_orders(
    start_date: Optional[date] = Query(None),
    end_date: Optional[date] = Query(None)
):
    """Get all orders or filter by date range"""
    if start_date and end_date:
        return service.get_orders_between_dates(start_date, end_date)
    return service.get_all_orders()


@router.get("/orders/{order_id}", response_model=Order)
async def get_order(order_id: int):
    """Get order by ID"""
    order = service.get_order_by_id(order_id)
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    return order


@router.post("/orders", response_model=Order)
async def create_order(order: OrderCreate):
    """Create a new order"""
    try:
        return service.create_order(order)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.put("/orders/{order_id}/status", response_model=Order)
async def update_order_status(order_id: int, status_update: OrderStatusUpdate):
    """Update order status"""
    # Validate status value
    valid_statuses = ['Pending', 'Shipped', 'Delivered', 'Canceled']
    if status_update.status not in valid_statuses:
        raise HTTPException(
            status_code=400, 
            detail=f"Invalid status. Must be one of: {', '.join(valid_statuses)}"
        )
    
    updated = service.update_order_status(order_id, status_update.status)
    if not updated:
        raise HTTPException(status_code=404, detail="Order not found")
    return updated


@router.post("/orders/{order_id}/cancel")
async def cancel_order(order_id: int):
    """Cancel order"""
    canceled = service.cancel_order(order_id)
    if not canceled:
        raise HTTPException(status_code=400, detail="Order cannot be canceled or not found")
    return canceled


@router.post("/reviews", response_model=Review)
async def create_review(review: ReviewCreate):
    """Create a new review"""
    try:
        return service.add_review(review)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))


@router.get("/analytics/top-products", response_model=List[Product])
async def get_top_products(limit: int = Query(3, ge=1, le=10)):
    """Get top products by rating"""
    return service.get_top_products_by_rating(limit)


@router.get("/analytics/customers-who-reviewed/{product_id}")
async def get_customers_who_reviewed_product(product_id: int):
    """Get customers who reviewed a product"""
    product = service.get_product_by_id(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return service.get_customers_who_reviewed_product(product_id)


@router.get("/analytics/common-products", response_model=List[Product])
async def get_common_high_rated_products(
    customer_id1: int = Query(...),
    customer_id2: int = Query(...)
):
    """Get common high-rated products between two customers"""
    customer1 = service.get_customer_by_id(customer_id1)
    customer2 = service.get_customer_by_id(customer_id2)
    
    if not customer1:
        raise HTTPException(status_code=404, detail=f"Customer {customer_id1} not found")
    if not customer2:
        raise HTTPException(status_code=404, detail=f"Customer {customer_id2} not found")
    
    return service.get_common_high_rated_products(customer_id1, customer_id2)


@router.get("/health")
async def health_check():
    """Health check endpoint"""
    products = service.get_all_products()
    customers = service.get_all_customers()
    orders = service.get_all_orders()
    
    total_reviews = sum(len(p.reviews) for p in products)
    
    return {
        "status": "healthy",
        "products": len(products),
        "customers": len(customers),
        "orders": len(orders),
        "reviews": total_reviews
    }
