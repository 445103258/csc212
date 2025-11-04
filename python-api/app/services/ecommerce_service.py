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

    def create_product(self, product_create: ProductCreate) -> Product:
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

    def get_customer_by_id(self, customer_id: int) -> Optional[Customer]:
        for c in self.customers_data:
            if c['customerId'] == customer_id:
                return Customer(**c)
        return None

    def create_customer(self, customer_create: CustomerCreate) -> Customer:
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

    def get_customer_orders(self, customer_id: int) -> List[Order]:
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

    def create_order(self, order_create: OrderCreate) -> Optional[Order]:
        customer = self.get_customer_by_id(order_create.customer_id)
        if not customer:
            return None

        total_price = 0.0
        for pid in order_create.product_ids:
            product = self.get_product_by_id(pid)
            if not product or product.stock < 1:
                return None
            total_price += product.price

        new_id = max([o['orderId'] for o in self.orders_data], default=300) + 1
        new_order = {
            'orderId': new_id,
            'customerId': order_create.customer_id,
            'productIds': order_create.product_ids,
            'totalPrice': total_price,
            'orderDate': date.today().isoformat(),
            'status': OrderStatus.PENDING.value
        }
        
        self.orders_data.append(new_order)
        self.repository.write_orders(self.orders_data)

        for pid in order_create.product_ids:
            for p in self.products_data:
                if p['productId'] == pid:
                    p['stock'] -= 1
        self.repository.write_products(self.products_data)

        for c in self.customers_data:
            if c['customerId'] == order_create.customer_id:
                c['orderIds'].append(new_id)
        self.repository.write_customers(self.customers_data)

        return Order(**new_order)

    def update_order_status(self, order_id: int, status: OrderStatus) -> Optional[Order]:
        for o in self.orders_data:
            if o['orderId'] == order_id:
                o['status'] = status.value
                self.repository.write_orders(self.orders_data)
                return Order(**o)
        return None

    def cancel_order(self, order_id: int) -> Optional[Order]:
        return self.update_order_status(order_id, OrderStatus.CANCELED)

    def get_orders_between_dates(self, start_date: date, end_date: date) -> List[Order]:
        orders = []
        for o in self.orders_data:
            order_date = datetime.strptime(o['orderDate'], '%Y-%m-%d').date()
            if start_date <= order_date <= end_date:
                orders.append(Order(**o))
        return orders

    def create_review(self, review_create: ReviewCreate) -> Optional[Review]:
        product = self.get_product_by_id(review_create.product_id)
        customer = self.get_customer_by_id(review_create.customer_id)
        
        if not product or not customer:
            return None

        new_id = max([r['reviewId'] for r in self.reviews_data], default=400) + 1
        new_review = {
            'reviewId': new_id,
            'productId': review_create.product_id,
            'customerId': review_create.customer_id,
            'rating': review_create.rating,
            'comment': review_create.comment
        }
        
        self.reviews_data.append(new_review)
        self.repository.write_reviews(self.reviews_data)
        
        self._link_reviews_to_products()
        
        return Review(**new_review)

    def get_top_products_by_rating(self, limit: int = 3) -> List[Product]:
        products_with_reviews = [p for p in self.products_data if p['reviews']]
        sorted_products = sorted(products_with_reviews, 
                                key=lambda p: p['averageRating'], 
                                reverse=True)
        return [Product(**p) for p in sorted_products[:limit]]

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
