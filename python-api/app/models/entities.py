from pydantic import BaseModel, Field
from typing import List, Optional
from datetime import date
from enum import Enum


class OrderStatus(str, Enum):
    PENDING = "Pending"
    SHIPPED = "Shipped"
    DELIVERED = "Delivered"
    CANCELED = "Canceled"


class Review(BaseModel):
    reviewId: int = Field(..., alias="reviewId")
    productId: int = Field(..., alias="productId")
    customerId: int = Field(..., alias="customerId")
    rating: int = Field(..., ge=1, le=5)
    comment: str

    class Config:
        populate_by_name = True


class Product(BaseModel):
    productId: int = Field(..., alias="productId")
    name: str
    price: float
    stock: int
    reviews: List[Review] = []
    averageRating: Optional[float] = Field(None, alias="averageRating")

    class Config:
        populate_by_name = True


class Customer(BaseModel):
    customerId: int = Field(..., alias="customerId")
    name: str
    email: str
    orderIds: List[int] = Field(default_factory=list, alias="orderIds")

    class Config:
        populate_by_name = True


class Order(BaseModel):
    orderId: int = Field(..., alias="orderId")
    customerId: int = Field(..., alias="customerId")
    productIds: List[int] = Field(..., alias="productIds")
    totalPrice: float = Field(..., alias="totalPrice")
    orderDate: date = Field(..., alias="orderDate")
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
    customerId: int = Field(..., alias="customerId")
    productIds: List[int] = Field(..., alias="productIds")

    class Config:
        populate_by_name = True


class ReviewCreate(BaseModel):
    productId: int = Field(..., alias="productId")
    customerId: int = Field(..., alias="customerId")
    rating: int = Field(..., ge=1, le=5)
    comment: str

    class Config:
        populate_by_name = True
