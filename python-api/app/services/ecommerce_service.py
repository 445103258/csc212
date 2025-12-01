from typing import List, Optional
from datetime import date, datetime
from app.repositories.csv_repository import CSVRepository
from app.models.entities import (
    Product, Customer, Order, Review,
    ProductCreate, CustomerCreate, OrderCreate, ReviewCreate,
    OrderStatus
)


class ECommerceService:
    def __init__(self, repository: CSVRepository):
        self.repository = repository
        self._load_data()

    def _load_data(self):
        self.products_data = self.repository.read_products()
        self.customers_data = self.repository.read_customers()
        self.orders_data = self.repository.read_orders()
        self.reviews_data = self.repository.read_reviews()
        
        self._link_reviews_to_products()
        self._link_orders_to_customers()

    def _link_reviews_to_products(self):
        for product in self.products_data:
            product['reviews'] = []
            for review in self.reviews_data:
                if review['productId'] == product['productId']:
                    product['reviews'].append(review)
            
            if product['reviews']:
                total_rating = sum(r['rating'] for r in product['reviews'])
                product['averageRating'] = total_rating / len(product['reviews'])
            else:
                product['averageRating'] = 0.0

    def _link_orders_to_customers(self):
        for customer in self.customers_data:
            customer['orderIds'] = []
            for order in self.orders_data:
                if order['customerId'] == customer['customerId']:
                    customer['orderIds'].append(order['orderId'])

    def get_all_products(self) -> List[Product]:
        return [Product(**p) for p in self.products_data]

    def get_product_by_id(self, product_id: int) -> Optional[Product]:
        for p in self.products_data:
            if p['productId'] == product_id:
                return Product(**p)
        return None

    def search_products_by_name(self, name: str) -> List[Product]:
        results = []
        name_lower = name.lower()
        for p in self.products_data:
            if name_lower in p['name'].lower():
                results.append(Product(**p))
        return results

    def get_products_by_price_range(self, min_price: float, max_price: float) -> List[Product]:
        """Get products within a price range"""
        results = []
        for p in self.products_data:
            if min_price <= p['price'] <= max_price:
                results.append(Product(**p))
        return results

    def add_product(self, product_create: ProductCreate) -> Product:
        new_id = max([p['productId'] for p in self.products_data], default=100) + 1
        new_product = {
            'productId': new_id,
            'name': product_create.name,
            'price': product_create.price,
            'stock': product_create.stock,
            'reviews': [],
            'averageRating': 0.0
        }
        self.products_data.append(new_product)
        self.repository.write_products(self.products_data)
        return Product(**new_product)

    def update_product(self, product_id: int, product_create: ProductCreate) -> Optional[Product]:
        for p in self.products_data:
            if p['productId'] == product_id:
                p['name'] = product_create.name
                p['price'] = product_create.price
                p['stock'] = product_create.stock
                self.repository.write_products(self.products_data)
                return Product(**p)
        return None

    def delete_product(self, product_id: int) -> bool:
        for i, p in enumerate(self.products_data):
            if p['productId'] == product_id:
                self.products_data.pop(i)
                self.repository.write_products(self.products_data)
                return True
        return False

    def get_out_of_stock_products(self) -> List[Product]:
        return [Product(**p) for p in self.products_data if p['stock'] == 0]

    def get_all_customers(self) -> List[Customer]:
        return [Customer(**c) for c in self.customers_data]

    def get_all_customers_sorted(self) -> List[Customer]:
        """Get all customers sorted alphabetically by name"""
        sorted_customers = sorted(self.customers_data, key=lambda c: c['name'].lower())
        return [Customer(**c) for c in sorted_customers]

    def get_customer_by_id(self, customer_id: int) -> Optional[Customer]:
        for c in self.customers_data:
            if c['customerId'] == customer_id:
                return Customer(**c)
        return None

    def register_customer(self, customer_create: CustomerCreate) -> Customer:
        new_id = max([c['customerId'] for c in self.customers_data], default=200) + 1
        new_customer = {
            'customerId': new_id,
            'name': customer_create.name,
            'email': customer_create.email,
            'orderIds': []
        }
        self.customers_data.append(new_customer)
        self.repository.write_customers(self.customers_data)
        return Customer(**new_customer)

    def update_customer(self, customer_id: int, customer_create: CustomerCreate) -> Optional[Customer]:
        for c in self.customers_data:
            if c['customerId'] == customer_id:
                c['name'] = customer_create.name
                c['email'] = customer_create.email
                self.repository.write_customers(self.customers_data)
                return Customer(**c)
        return None

    def delete_customer(self, customer_id: int) -> bool:
        for i, c in enumerate(self.customers_data):
            if c['customerId'] == customer_id:
                self.customers_data.pop(i)
                self.repository.write_customers(self.customers_data)
                return True
        return False

    def get_customer_order_history(self, customer_id: int) -> List[Order]:
        orders = []
        for o in self.orders_data:
            if o['customerId'] == customer_id:
                orders.append(Order(**o))
        return orders

    def get_customer_reviews(self, customer_id: int) -> List[Review]:
        reviews = []
        for r in self.reviews_data:
            if r['customerId'] == customer_id:
                reviews.append(Review(**r))
        return reviews

    def get_all_orders(self) -> List[Order]:
        return [Order(**o) for o in self.orders_data]

    def get_order_by_id(self, order_id: int) -> Optional[Order]:
        for o in self.orders_data:
            if o['orderId'] == order_id:
                return Order(**o)
        return None

    def create_order(self, order_create: OrderCreate) -> Order:
        customer = self.get_customer_by_id(order_create.customerId)
        if not customer:
            raise ValueError("Customer not found")

        total_price = 0.0
        for pid in order_create.productIds:
            product = self.get_product_by_id(pid)
            if not product:
                raise ValueError(f"Product {pid} not found")
            if product.stock < 1:
                raise ValueError(f"Product {product.name} is out of stock")
            total_price += product.price

        new_id = max([o['orderId'] for o in self.orders_data], default=300) + 1
        new_order = {
            'orderId': new_id,
            'customerId': order_create.customerId,
            'productIds': order_create.productIds,
            'totalPrice': total_price,
            'orderDate': date.today().isoformat(),
            'status': OrderStatus.PENDING.value
        }
        
        self.orders_data.append(new_order)
        self.repository.write_orders(self.orders_data)

        # Update product stock
        for pid in order_create.productIds:
            for p in self.products_data:
                if p['productId'] == pid:
                    p['stock'] -= 1
        self.repository.write_products(self.products_data)

        # Update customer order IDs
        for c in self.customers_data:
            if c['customerId'] == order_create.customerId:
                c['orderIds'].append(new_id)
        self.repository.write_customers(self.customers_data)

        return Order(**new_order)

    def update_order_status(self, order_id: int, status: str) -> Optional[Order]:
        for o in self.orders_data:
            if o['orderId'] == order_id:
                o['status'] = status
                self.repository.write_orders(self.orders_data)
                return Order(**o)
        return None

    def cancel_order(self, order_id: int) -> Optional[Order]:
        return self.update_order_status(order_id, OrderStatus.CANCELED.value)

    def get_orders_between_dates(self, start_date: date, end_date: date) -> List[Order]:
        orders = []
        for o in self.orders_data:
            order_date = datetime.strptime(o['orderDate'], '%Y-%m-%d').date()
            if start_date <= order_date <= end_date:
                orders.append(Order(**o))
        return orders

    def add_review(self, review_create: ReviewCreate) -> Review:
        product = self.get_product_by_id(review_create.productId)
        customer = self.get_customer_by_id(review_create.customerId)
        
        if not product:
            raise ValueError("Product not found")
        if not customer:
            raise ValueError("Customer not found")

        new_id = max([r['reviewId'] for r in self.reviews_data], default=400) + 1
        new_review = {
            'reviewId': new_id,
            'productId': review_create.productId,
            'customerId': review_create.customerId,
            'rating': review_create.rating,
            'comment': review_create.comment
        }
        
        self.reviews_data.append(new_review)
        self.repository.write_reviews(self.reviews_data)
        
        # Reload data to update average ratings
        self._link_reviews_to_products()
        
        return Review(**new_review)

    def get_top_products_by_rating(self, limit: int = 3) -> List[Product]:
        products_with_reviews = [p for p in self.products_data if p['reviews']]
        sorted_products = sorted(products_with_reviews, 
                                key=lambda p: p['averageRating'], 
                                reverse=True)
        return [Product(**p) for p in sorted_products[:limit]]

    def get_customers_who_reviewed_product(self, product_id: int) -> List[Customer]:
        """Get customers who reviewed a specific product"""
        customer_ids = set()
        
        # Find all unique customer IDs who reviewed this product
        for review in self.reviews_data:
            if review['productId'] == product_id:
                customer_ids.add(review['customerId'])
        
        # Return full Customer objects for these customers
        customers = []
        for customer_id in customer_ids:
            customer = self.get_customer_by_id(customer_id)
            if customer:
                customers.append(customer)
        
        return customers

    def get_common_high_rated_products(self, customer_id1: int, customer_id2: int) -> List[Product]:
        common_products = []
        
        for p in self.products_data:
            customer1_reviews = [r for r in p['reviews'] if r['customerId'] == customer_id1]
            customer2_reviews = [r for r in p['reviews'] if r['customerId'] == customer_id2]
            
            if customer1_reviews and customer2_reviews:
                all_reviews = customer1_reviews + customer2_reviews
                avg_rating = sum(r['rating'] for r in all_reviews) / len(all_reviews)
                
                if avg_rating > 4.0:
                    common_products.append(Product(**p))
        
        return common_products
