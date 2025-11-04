from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from app.views.api_routes import router

app = FastAPI(
    title="E-Commerce Inventory & Order Management API",
    description="Backend API for managing products, customers, orders, and reviews",
    version="1.0.0"
)

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(router, prefix="/api/v1", tags=["ecommerce"])


@app.get("/")
async def root():
    return {
        "message": "E-Commerce Inventory & Order Management System API",
        "version": "1.0.0",
        "docs": "/docs",
        "health": "/api/v1/health"
    }
