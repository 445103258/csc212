from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date
from enum import Enum


class OrderStatus(str, Enum):
    PENDING = "Pending"
    SHIPPED = "Shipped"
    DELIVERED = "Delivered"
    CANCELED = "Cancelled"


class Review(BaseModel):
    review_id: int = Field(..., alias="reviewId")
    product_id: int = Field(..., alias="productId")
    customer_id: int = Field(..., alias="customerId")
    rating: int = Field(..., ge=1, le=5)
    comment: str

    class Config:
        populate_by_name = True


class Product(BaseModel):
    product_id: int = Field(..., alias="productId")
    name: str
    price: float
    stock: int
    reviews: List[Review] = []
    average_rating: Optional[float] = Field(None, alias="averageRating")

    class Config:
        populate_by_name = True


class Customer(BaseModel):
    customer_id: int = Field(..., alias="customerId")
    name: str
    email: str
    order_ids: List[int] = Field(default_factory=list, alias="orderIds")

    class Config:
        populate_by_name = True


class Order(BaseModel):
    order_id: int = Field(..., alias="orderId")
    customer_id: int = Field(..., alias="customerId")
    product_ids: List[int] = Field(..., alias="productIds")
    total_price: float = Field(..., alias="totalPrice")
    order_date: date = Field(..., alias="orderDate")
    status: OrderStatus

    class Config:
        populate_by_name = True


class ProductCreate(BaseModel):
    name: str
    price: float
    stock: int


class CustomerCreate(BaseModel):
    name: str
    email: str


class OrderCreate(BaseModel):
    customer_id: int = Field(..., alias="customerId")
    product_ids: List[int] = Field(..., alias="productIds")

    class Config:
        populate_by_name = True


class ReviewCreate(BaseModel):
    product_id: int = Field(..., alias="productId")
    customer_id: int = Field(..., alias="customerId")
    rating: int = Field(..., ge=1, le=5)
    comment: str

    class Config:
        populate_by_name = True
