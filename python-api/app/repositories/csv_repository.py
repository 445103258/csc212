import csv
from typing import List, Dict, Any
from pathlib import Path


class CSVRepository:
    def __init__(self, data_dir: str = "data"):
        self.data_dir = Path(data_dir)
        self.products_file = self.data_dir / "products.csv"
        self.customers_file = self.data_dir / "customers.csv"
        self.orders_file = self.data_dir / "orders.csv"
        self.reviews_file = self.data_dir / "reviews.csv"

    def read_products(self) -> List[Dict[str, Any]]:
        products = []
        try:
            with open(self.products_file, 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                for row in reader:
                    products.append({
                        'productId': int(row['productId']),
                        'name': row['name'],
                        'price': float(row['price']),
                        'stock': int(row['stock'])
                    })
        except FileNotFoundError:
            print(f"File not found: {self.products_file}")
        return products

    def read_customers(self) -> List[Dict[str, Any]]:
        customers = []
        try:
            with open(self.customers_file, 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                for row in reader:
                    customers.append({
                        'customerId': int(row['customerId']),
                        'name': row['name'],
                        'email': row['email']
                    })
        except FileNotFoundError:
            print(f"File not found: {self.customers_file}")
        return customers

    def read_orders(self) -> List[Dict[str, Any]]:
        orders = []
        try:
            with open(self.orders_file, 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                for row in reader:
                    product_ids_str = row['productIds'].strip('"')
                    product_ids = [int(pid.strip()) for pid in product_ids_str.split(';')]
                    
                    orders.append({
                        'orderId': int(row['orderId']),
                        'customerId': int(row['customerId']),
                        'productIds': product_ids,
                        'totalPrice': float(row['totalPrice']),
                        'orderDate': row['orderDate'],
                        'status': row['status']
                    })
        except FileNotFoundError:
            print(f"File not found: {self.orders_file}")
        return orders

    def read_reviews(self) -> List[Dict[str, Any]]:
        reviews = []
        try:
            with open(self.reviews_file, 'r', encoding='utf-8') as f:
                reader = csv.DictReader(f)
                for row in reader:
                    reviews.append({
                        'reviewId': int(row['reviewId']),
                        'productId': int(row['productId']),
                        'customerId': int(row['customerId']),
                        'rating': int(row['rating']),
                        'comment': row['comment'].strip('"')
                    })
        except FileNotFoundError:
            print(f"File not found: {self.reviews_file}")
        return reviews

    def write_products(self, products: List[Dict[str, Any]]):
        with open(self.products_file, 'w', newline='', encoding='utf-8') as f:
            fieldnames = ['productId', 'name', 'price', 'stock', 'reviews', 'averageRating']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(products)

    def write_customers(self, customers: List[Dict[str, Any]]):
        with open(self.customers_file, 'w', newline='', encoding='utf-8') as f:
            fieldnames = ['customerId', 'name', 'email', 'orderIds']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            writer.writerows(customers)

    def write_orders(self, orders: List[Dict[str, Any]]):
        with open(self.orders_file, 'w', newline='', encoding='utf-8') as f:
            fieldnames = ['orderId', 'customerId', 'productIds', 'totalPrice', 'orderDate', 'status']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            
            for order in orders:
                order_copy = order.copy()
                product_ids = order_copy['productIds']
                if isinstance(product_ids, list):
                    order_copy['productIds'] = f'"{";".join(map(str, product_ids))}"'
                writer.writerow(order_copy)

    def write_reviews(self, reviews: List[Dict[str, Any]]):
        with open(self.reviews_file, 'w', newline='', encoding='utf-8') as f:
            fieldnames = ['reviewId', 'productId', 'customerId', 'rating', 'comment']
            writer = csv.DictWriter(f, fieldnames=fieldnames)
            writer.writeheader()
            
            for review in reviews:
                review_copy = review.copy()
                if 'comment' in review_copy:
                    review_copy['comment'] = f'"{review_copy["comment"]}"'
                writer.writerow(review_copy)
