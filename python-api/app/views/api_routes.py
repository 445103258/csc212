from fastapi import APIRouter, HTTPException, Query
from typing import List, Optional
from datetime import date
from app.models.entities import (
    Product, Customer, Order, Review,
    ProductCreate, CustomerCreate, OrderCreate, ReviewCreate,
    OrderStatus
)
from app.services.ecommerce_service import ECommerceService
from app.repositories.csv_repository import CSVRepository

router = APIRouter()

repository = CSVRepository()
service = ECommerceService(repository)


@router.get("/products", response_model=List[Product])
async def get_products(name: Optional[str] = None):
    if name:
        return service.search_products_by_name(name)
    return service.get_all_products()


@router.get("/products/{product_id}", response_model=Product)
async def get_product(product_id: int):
    product = service.get_product_by_id(product_id)
    if not product:
        raise HTTPException(status_code=404, detail="Product not found")
    return product


@router.post("/products", response_model=Product)
async def create_product(product: ProductCreate):
    return service.create_product(product)


@router.put("/products/{product_id}", response_model=Product)
async def update_product(product_id: int, product: ProductCreate):
    updated = service.update_product(product_id, product)
    if not updated:
        raise HTTPException(status_code=404, detail="Product not found")
    return updated


@router.delete("/products/{product_id}")
async def delete_product(product_id: int):
    success = service.delete_product(product_id)
    if not success:
        raise HTTPException(status_code=404, detail="Product not found")
    return {"message": "Product deleted successfully"}


@router.get("/products/stock/out-of-stock", response_model=List[Product])
async def get_out_of_stock_products():
    return service.get_out_of_stock_products()


@router.get("/customers", response_model=List[Customer])
async def get_customers():
    return service.get_all_customers()


@router.get("/customers/{customer_id}", response_model=Customer)
async def get_customer(customer_id: int):
    customer = service.get_customer_by_id(customer_id)
    if not customer:
        raise HTTPException(status_code=404, detail="Customer not found")
    return customer


@router.post("/customers", response_model=Customer)
async def create_customer(customer: CustomerCreate):
    return service.create_customer(customer)


@router.get("/customers/{customer_id}/orders", response_model=List[Order])
async def get_customer_orders(customer_id: int):
    return service.get_customer_orders(customer_id)


@router.get("/customers/{customer_id}/reviews", response_model=List[Review])
async def get_customer_reviews(customer_id: int):
    return service.get_customer_reviews(customer_id)


@router.get("/orders", response_model=List[Order])
async def get_orders(
    start_date: Optional[date] = Query(None),
    end_date: Optional[date] = Query(None)
):
    if start_date and end_date:
        return service.get_orders_between_dates(start_date, end_date)
    return service.get_all_orders()


@router.get("/orders/{order_id}", response_model=Order)
async def get_order(order_id: int):
    order = service.get_order_by_id(order_id)
    if not order:
        raise HTTPException(status_code=404, detail="Order not found")
    return order


@router.post("/orders", response_model=Order)
async def create_order(order: OrderCreate):
    created_order = service.create_order(order)
    if not created_order:
        raise HTTPException(status_code=400, detail="Failed to create order. Check customer and product availability.")
    return created_order


@router.patch("/orders/{order_id}/status")
async def update_order_status(order_id: int, status: OrderStatus):
    updated = service.update_order_status(order_id, status)
    if not updated:
        raise HTTPException(status_code=404, detail="Order not found")
    return updated


@router.post("/orders/{order_id}/cancel")
async def cancel_order(order_id: int):
    canceled = service.cancel_order(order_id)
    if not canceled:
        raise HTTPException(status_code=404, detail="Order not found or cannot be canceled")
    return canceled


@router.post("/reviews", response_model=Review)
async def create_review(review: ReviewCreate):
    created_review = service.create_review(review)
    if not created_review:
        raise HTTPException(status_code=400, detail="Failed to create review. Check product and customer IDs.")
    return created_review


@router.get("/analytics/top-products", response_model=List[Product])
async def get_top_products(limit: int = Query(3, ge=1, le=10)):
    return service.get_top_products_by_rating(limit)


@router.get("/analytics/common-products", response_model=List[Product])
async def get_common_high_rated_products(
    customer_id1: int = Query(...),
    customer_id2: int = Query(...)
):
    return service.get_common_high_rated_products(customer_id1, customer_id2)


@router.get("/health")
async def health_check():
    return {
        "status": "healthy",
        "products": len(service.products_data),
        "customers": len(service.customers_data),
        "orders": len(service.orders_data),
        "reviews": len(service.reviews_data)
    }
