import { useState, useEffect } from 'react';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from '@/components/ui/table';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Command, CommandEmpty, CommandGroup, CommandInput, CommandItem, CommandList } from '@/components/ui/command';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Package, Users, ShoppingCart, Star, TrendingUp, AlertCircle, Search, Edit, Trash2, X, Check, ChevronsUpDown } from 'lucide-react';
import { toast } from 'sonner';
import { cn } from '@/lib/utils';

const API_BASE_URL = 'http://localhost:8000/api/v1';

interface Product {
  productId: number;
  name: string;
  price: number;
  stock: number;
  averageRating: number;
  reviews: Review[];
}

interface Customer {
  customerId: number;
  name: string;
  email: string;
  orderIds: number[];
}

interface Order {
  orderId: number;
  customerId: number;
  productIds: number[];
  totalPrice: number;
  orderDate: string;
  status: string;
}

interface Review {
  reviewId: number;
  productId: number;
  customerId: number;
  rating: number;
  comment: string;
}

interface Stats {
  products: number;
  customers: number;
  orders: number;
  reviews: number;
}

export default function Dashboard() {
  const [products, setProducts] = useState<Product[]>([]);
  const [customers, setCustomers] = useState<Customer[]>([]);
  const [orders, setOrders] = useState<Order[]>([]);
  const [topProducts, setTopProducts] = useState<Product[]>([]);
  const [stats, setStats] = useState<Stats>({ products: 0, customers: 0, orders: 0, reviews: 0 });
  const [loading, setLoading] = useState(true);

  const [newProduct, setNewProduct] = useState({ name: '', price: '', stock: '' });
  const [newCustomer, setNewCustomer] = useState({ name: '', email: '' });
  const [newOrder, setNewOrder] = useState({ customerId: 0, productIds: [] as number[] });
  const [newReview, setNewReview] = useState({ productId: 0, customerId: 0, rating: 5, comment: '' });
  const [hoveredStar, setHoveredStar] = useState<number | null>(null);

  const [editingProduct, setEditingProduct] = useState<Product | null>(null);
  const [editingReview, setEditingReview] = useState<{ review: Review; product: Product } | null>(null);
  const [searchProductId, setSearchProductId] = useState('');
  const [searchOrderId, setSearchOrderId] = useState('');
  const [openOrderCombobox, setOpenOrderCombobox] = useState(false);
  const [selectedCustomerHistory, setSelectedCustomerHistory] = useState<Customer | null>(null);
  const [customerOrders, setCustomerOrders] = useState<Order[]>([]);

  const [openProductCombobox, setOpenProductCombobox] = useState(false);
  const [openCustomerCombobox, setOpenCustomerCombobox] = useState(false);
  const [openOrderProductsCombobox, setOpenOrderProductsCombobox] = useState(false);
  const [openReviewProductCombobox, setOpenReviewProductCombobox] = useState(false);
  const [openReviewCustomerCombobox, setOpenReviewCustomerCombobox] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setLoading(true);
      const [productsRes, customersRes, ordersRes, topProductsRes, statsRes] = await Promise.all([
        fetch(`${API_BASE_URL}/products`),
        fetch(`${API_BASE_URL}/customers`),
        fetch(`${API_BASE_URL}/orders`),
        fetch(`${API_BASE_URL}/analytics/top-products?limit=3`),
        fetch(`${API_BASE_URL}/health`)
      ]);

      setProducts(await productsRes.json());
      setCustomers(await customersRes.json());
      setOrders(await ordersRes.json());
      setTopProducts(await topProductsRes.json());
      
      const statsData = await statsRes.json();
      setStats({
        products: statsData.products,
        customers: statsData.customers,
        orders: statsData.orders,
        reviews: statsData.reviews
      });
    } catch (error) {
      toast.error('Failed to fetch data. Make sure the Python API is running on port 8000.');
      console.error('Error fetching data:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddProduct = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/products`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: newProduct.name,
          price: parseFloat(newProduct.price),
          stock: parseInt(newProduct.stock)
        })
      });
      
      if (response.ok) {
        toast.success('Product added successfully!');
        setNewProduct({ name: '', price: '', stock: '' });
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to add product');
    }
  };

  const handleUpdateProduct = async () => {
    if (!editingProduct) return;
    try {
      const response = await fetch(`${API_BASE_URL}/products/${editingProduct.productId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: editingProduct.name,
          price: editingProduct.price,
          stock: editingProduct.stock
        })
      });
      
      if (response.ok) {
        toast.success('Product updated successfully!');
        setEditingProduct(null);
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to update product');
    }
  };

  const handleDeleteProduct = async (productId: number) => {
    if (!confirm('Are you sure you want to delete this product?')) return;
    try {
      const response = await fetch(`${API_BASE_URL}/products/${productId}`, {
        method: 'DELETE'
      });
      
      if (response.ok) {
        toast.success('Product deleted successfully!');
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to delete product');
    }
  };

  const handleSearchProduct = () => {
    const product = products.find(p => p.productId === parseInt(searchProductId));
    if (product) {
      toast.success(`Found: ${product.name} - $${product.price} - Stock: ${product.stock}`);
    } else {
      toast.error('Product not found');
    }
  };

  const handleAddCustomer = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/customers`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(newCustomer)
      });
      
      if (response.ok) {
        toast.success('Customer registered successfully!');
        setNewCustomer({ name: '', email: '' });
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to register customer');
    }
  };

  const handleViewCustomerHistory = async (customer: Customer) => {
    setSelectedCustomerHistory(customer);
    try {
      const response = await fetch(`${API_BASE_URL}/customers/${customer.customerId}/orders`);
      const orders = await response.json();
      setCustomerOrders(orders);
    } catch (error) {
      toast.error('Failed to fetch customer orders');
    }
  };

  const handlePlaceOrder = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/orders`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          customerId: newOrder.customerId,
          productIds: newOrder.productIds
        })
      });
      
      if (response.ok) {
        toast.success('Order placed successfully!');
        setNewOrder({ customerId: 0, productIds: [] });
        fetchData();
      } else {
        toast.error('Failed to place order. Check customer ID and product availability.');
      }
    } catch (error) {
      toast.error('Failed to place order');
    }
  };

  const handleUpdateOrderStatus = async (orderId: number, newStatus: string) => {
      const query = new URLSearchParams({ status: newStatus }); 
      try {
	  const response = await fetch(`${API_BASE_URL}/orders/${orderId}/status?${query.toString()}`, {
	      method: 'PATCH',
	      headers: { 'Content-Type': 'application/json' },
	  });
	  
	  if (response.ok) {
	      toast.success(`Order #${orderId} status updated to ${newStatus}!`);
	      fetchData();
	  } else {
	      toast.error('Failed to update order status.');
	      console.error('API Error:', await response.text());
	  }
      } catch (error) {
	  toast.error('An error occurred while updating the order status.');
	  console.error('Fetch Error:', error);
      }
  };
    
  const handleCancelOrder = async (orderId: number) => {
    if (!confirm('Are you sure you want to cancel this order?')) return;
    try {
      const response = await fetch(`${API_BASE_URL}/orders/${orderId}/cancel`, {
        method: 'POST'
      });
      
      if (response.ok) {
        toast.success('Order canceled successfully!');
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to cancel order');
    }
  };

  const handleSearchOrder = () => {
    const order = orders.find(o => o.orderId === parseInt(searchOrderId));
    if (order) {
      toast.success(`Found Order #${order.orderId} - Customer: ${order.customerId} - Total: $${order.totalPrice.toFixed(2)}`);
    } else {
      toast.error('Order not found');
    }
  };

  const handleAddReview = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/reviews`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          productId: newReview.productId,
          customerId: newReview.customerId,
          rating: newReview.rating,
          comment: newReview.comment
        })
      });
      
      if (response.ok) {
        toast.success('Review added successfully!');
        setNewReview({ productId: 0, customerId: 0, rating: 5, comment: '' });
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to add review');
    }
  };

  const handleEditReview = async () => {
    if (!editingReview) return;
    try {
      const response = await fetch(`${API_BASE_URL}/reviews`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          productId: editingReview.review.productId,
          customerId: editingReview.review.customerId,
          rating: editingReview.review.rating,
          comment: editingReview.review.comment
        })
      });
      
      if (response.ok) {
        toast.success('Review updated successfully!');
        setEditingReview(null);
        fetchData();
      }
    } catch (error) {
      toast.error('Failed to update review');
    }
  };

  const getStatusBadge = (status: string) => {
    const colors: { [key: string]: string } = {
      'Pending': 'bg-yellow-500',
      'Shipped': 'bg-blue-500',
      'Delivered': 'bg-green-500',
      'Cancelled': 'bg-red-500'
    };
    return <Badge className={colors[status] || 'bg-gray-500'}>{status}</Badge>;
  };

  const outOfStockProducts = products.filter(p => p.stock === 0);

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gradient-to-br from-slate-50 to-slate-100">
        <div className="text-center">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-600 mx-auto mb-4"></div>
          <p className="text-slate-600">Loading dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-slate-100">
      <div className="container mx-auto p-6 space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h1 className="text-4xl font-bold bg-gradient-to-r from-blue-600 to-indigo-600 bg-clip-text text-transparent">
              E-Commerce Dashboard
            </h1>
            <p className="text-slate-600 mt-2">Inventory & Order Management System</p>
          </div>
          <Button onClick={fetchData} variant="outline">
            Refresh Data
          </Button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          <Card className="border-l-4 border-l-blue-500">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Total Products</CardTitle>
              <Package className="h-4 w-4 text-blue-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.products}</div>
            </CardContent>
          </Card>

          <Card className="border-l-4 border-l-green-500">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Total Customers</CardTitle>
              <Users className="h-4 w-4 text-green-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.customers}</div>
            </CardContent>
          </Card>

          <Card className="border-l-4 border-l-purple-500">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Total Orders</CardTitle>
              <ShoppingCart className="h-4 w-4 text-purple-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.orders}</div>
            </CardContent>
          </Card>

          <Card className="border-l-4 border-l-yellow-500">
            <CardHeader className="flex flex-row items-center justify-between pb-2">
              <CardTitle className="text-sm font-medium">Total Reviews</CardTitle>
              <Star className="h-4 w-4 text-yellow-500" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{stats.reviews}</div>
            </CardContent>
          </Card>
        </div>

        {outOfStockProducts.length > 0 && (
          <Card className="border-l-4 border-l-red-500">
            <CardHeader>
              <CardTitle className="flex items-center gap-2 text-red-600">
                <AlertCircle className="h-5 w-5" />
                Out of Stock Alert
              </CardTitle>
              <CardDescription>{outOfStockProducts.length} product(s) need restocking</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="space-y-2">
                {outOfStockProducts.map(product => (
                  <div key={product.productId} className="flex items-center justify-between p-3 bg-red-50 rounded-lg">
                    <div>
                      <p className="font-semibold text-red-900">{product.name}</p>
                      <p className="text-sm text-red-600">Product ID: {product.productId}</p>
                    </div>
                    <Badge variant="destructive">Out of Stock</Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}

        <Card className="border-t-4 border-t-indigo-500">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <TrendingUp className="h-5 w-5" />
              Top 3 Products by Rating
            </CardTitle>
            <CardDescription>Best performing products based on customer reviews</CardDescription>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {topProducts.map((product, index) => (
                <div key={product.productId} className="flex items-center justify-between p-4 bg-slate-50 rounded-lg">
                  <div className="flex items-center gap-4">
                    <div className="text-2xl font-bold text-slate-400">#{index + 1}</div>
                    <div>
                      <p className="font-semibold">{product.name}</p>
                      <p className="text-sm text-slate-600">${product.price.toFixed(2)}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2">
                    <Star className="h-5 w-5 fill-yellow-400 text-yellow-400" />
                    <span className="font-bold">{product.averageRating.toFixed(2)}</span>
                    <span className="text-sm text-slate-600">({product.reviews.length} reviews)</span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        <Tabs defaultValue="products" className="space-y-4">
          <TabsList className="grid w-full grid-cols-4">
            <TabsTrigger value="products">Products</TabsTrigger>
            <TabsTrigger value="customers">Customers</TabsTrigger>
            <TabsTrigger value="orders">Orders</TabsTrigger>
            <TabsTrigger value="reviews">Reviews</TabsTrigger>
          </TabsList>

          <TabsContent value="products" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Product Inventory</CardTitle>
                <CardDescription>Manage your product catalog</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex gap-2">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button>Add New Product</Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Add New Product</DialogTitle>
                        <DialogDescription>Enter product details below</DialogDescription>
                      </DialogHeader>
                      <div className="space-y-4">
                        <div>
                          <Label>Product Name</Label>
                          <Input
                            value={newProduct.name}
                            onChange={(e) => setNewProduct({ ...newProduct, name: e.target.value })}
                            placeholder="Enter product name"
                          />
                        </div>
                        <div>
                          <Label>Price</Label>
                          <Input
                            type="number"
                            step="0.01"
                            value={newProduct.price}
                            onChange={(e) => setNewProduct({ ...newProduct, price: e.target.value })}
                            placeholder="0.00"
                          />
                        </div>
                        <div>
                          <Label>Stock</Label>
                          <Input
                            type="number"
                            value={newProduct.stock}
                            onChange={(e) => setNewProduct({ ...newProduct, stock: e.target.value })}
                            placeholder="0"
                          />
                        </div>
                        <Button onClick={handleAddProduct} className="w-full">Add Product</Button>
                      </div>
                    </DialogContent>
                  </Dialog>

                  <div className="flex gap-2 flex-1">
                    <Popover open={openProductCombobox} onOpenChange={setOpenProductCombobox}>
                      <PopoverTrigger asChild>
                        <Button variant="outline" className="flex-1 justify-between">
                          {searchProductId ? `Product #${searchProductId}` : "Search by ID..."}
                          <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                        </Button>
                      </PopoverTrigger>
                      <PopoverContent className="w-[400px] p-0">
                        <Command>
                          <CommandInput placeholder="Search product..." />
                          <CommandList>
                            <CommandEmpty>No product found.</CommandEmpty>
                            <CommandGroup>
                              {products.map((product) => (
                                <CommandItem
                                  key={product.productId}
                                  value={`${product.productId} ${product.name}`}
                                  onSelect={() => {
                                    setSearchProductId(product.productId.toString());
                                    setOpenProductCombobox(false);
                                  }}
                                >
                                  <Check
                                    className={cn(
                                      "mr-2 h-4 w-4",
                                      searchProductId === product.productId.toString() ? "opacity-100" : "opacity-0"
                                    )}
                                  />
                                  <div className="flex-1">
                                    <div className="font-medium">{product.name}</div>
                                    <div className="text-sm text-muted-foreground">
                                      ID: {product.productId} | ${product.price} | Stock: {product.stock}
                                    </div>
                                  </div>
                                </CommandItem>
                              ))}
                            </CommandGroup>
                          </CommandList>
                        </Command>
                      </PopoverContent>
                    </Popover>
                    <Button onClick={handleSearchProduct} variant="secondary">
                      <Search className="h-4 w-4" />
                    </Button>
                  </div>
                </div>

                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Name</TableHead>
                      <TableHead>Price</TableHead>
                      <TableHead>Stock</TableHead>
                      <TableHead>Rating</TableHead>
                      <TableHead>Reviews</TableHead>
                      <TableHead>Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {products.map((product) => (
                      <TableRow key={product.productId}>
                        <TableCell>{product.productId}</TableCell>
                        <TableCell className="font-medium">{product.name}</TableCell>
                        <TableCell>${product.price.toFixed(2)}</TableCell>
                        <TableCell>
                          {product.stock === 0 ? (
                            <Badge variant="destructive" className="flex items-center gap-1 w-fit">
                              <AlertCircle className="h-3 w-3" />
                              Out of Stock
                            </Badge>
                          ) : (
                            <span>{product.stock}</span>
                          )}
                        </TableCell>
                        <TableCell>
                          <div className="flex items-center gap-1">
                            <Star className="h-4 w-4 fill-yellow-400 text-yellow-400" />
                            {product.averageRating.toFixed(2)}
                          </div>
                        </TableCell>
                        <TableCell>{product.reviews.length}</TableCell>
                        <TableCell>
                          <div className="flex gap-2">
                            <Button
                              size="sm"
                              variant="outline"
                              onClick={() => setEditingProduct(product)}
                            >
                              <Edit className="h-4 w-4" />
                            </Button>
                            <Button
                              size="sm"
                              variant="destructive"
                              onClick={() => handleDeleteProduct(product.productId)}
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="customers" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Customer Management</CardTitle>
                <CardDescription>View and manage customers</CardDescription>
              </CardHeader>
              <CardContent>
                <Dialog>
                  <DialogTrigger asChild>
                    <Button className="mb-4">Register New Customer</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Register New Customer</DialogTitle>
                      <DialogDescription>Enter customer details below</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                      <div>
                        <Label>Name</Label>
                        <Input
                          value={newCustomer.name}
                          onChange={(e) => setNewCustomer({ ...newCustomer, name: e.target.value })}
                          placeholder="Enter customer name"
                        />
                      </div>
                      <div>
                        <Label>Email</Label>
                        <Input
                          type="email"
                          value={newCustomer.email}
                          onChange={(e) => setNewCustomer({ ...newCustomer, email: e.target.value })}
                          placeholder="customer@example.com"
                        />
                      </div>
                      <Button onClick={handleAddCustomer} className="w-full">Register Customer</Button>
                    </div>
                  </DialogContent>
                </Dialog>

                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>ID</TableHead>
                      <TableHead>Name</TableHead>
                      <TableHead>Email</TableHead>
                      <TableHead>Orders</TableHead>
                      <TableHead>Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {customers.map((customer) => (
                      <TableRow key={customer.customerId}>
                        <TableCell>{customer.customerId}</TableCell>
                        <TableCell className="font-medium">{customer.name}</TableCell>
                        <TableCell>{customer.email}</TableCell>
                        <TableCell>{customer.orderIds.length}</TableCell>
                        <TableCell>
                          <Button
                            size="sm"
                            variant="outline"
                            onClick={() => handleViewCustomerHistory(customer)}
                          >
                            View History
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="orders" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Order Management</CardTitle>
                <CardDescription>Track and manage orders</CardDescription>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex gap-2">
                  <Dialog>
                    <DialogTrigger asChild>
                      <Button>Place New Order</Button>
                    </DialogTrigger>
                    <DialogContent>
                      <DialogHeader>
                        <DialogTitle>Place New Order</DialogTitle>
                        <DialogDescription>Select customer and products</DialogDescription>
                      </DialogHeader>
                      <div className="space-y-4">
                        <div>
                          <Label>Customer</Label>
                          <Popover open={openCustomerCombobox} onOpenChange={setOpenCustomerCombobox}>
                            <PopoverTrigger asChild>
                              <Button variant="outline" className="w-full justify-between">
                                {newOrder.customerId ? 
                                  customers.find(c => c.customerId === newOrder.customerId)?.name || "Select customer..." 
                                  : "Select customer..."}
                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                              </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-full p-0">
                              <Command>
                                <CommandInput placeholder="Search customer..." />
                                <CommandList>
                                  <CommandEmpty>No customer found.</CommandEmpty>
                                  <CommandGroup>
                                    {customers.map((customer) => (
                                      <CommandItem
                                        key={customer.customerId}
                                        value={`${customer.customerId} ${customer.name}`}
                                        onSelect={() => {
                                          setNewOrder({ ...newOrder, customerId: customer.customerId });
                                          setOpenCustomerCombobox(false);
                                        }}
                                      >
                                        <Check
                                          className={cn(
                                            "mr-2 h-4 w-4",
                                            newOrder.customerId === customer.customerId ? "opacity-100" : "opacity-0"
                                          )}
                                        />
                                        <div>
                                          <div className="font-medium">{customer.name}</div>
                                          <div className="text-sm text-muted-foreground">ID: {customer.customerId} | {customer.email}</div>
                                        </div>
                                      </CommandItem>
                                    ))}
                                  </CommandGroup>
                                </CommandList>
                              </Command>
                            </PopoverContent>
                          </Popover>
                        </div>
                        <div>
                          <Label>Products</Label>
                          <Popover open={openOrderProductsCombobox} onOpenChange={setOpenOrderProductsCombobox}>
                            <PopoverTrigger asChild>
                              <Button variant="outline" className="w-full justify-between">
                                {newOrder.productIds.length > 0 ? `${newOrder.productIds.length} product(s) selected` : "Select products..."}
                                <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                              </Button>
                            </PopoverTrigger>
                            <PopoverContent className="w-full p-0">
                              <Command>
                                <CommandInput placeholder="Search product..." />
                                <CommandList>
                                  <CommandEmpty>No product found.</CommandEmpty>
                                  <CommandGroup>
                                    {products.filter(p => p.stock > 0).map((product) => (
                                      <CommandItem
                                        key={product.productId}
                                        value={`${product.productId} ${product.name}`}
                                        onSelect={() => {
                                          const isSelected = newOrder.productIds.includes(product.productId);
                                          setNewOrder({
                                            ...newOrder,
                                            productIds: isSelected
                                              ? newOrder.productIds.filter(id => id !== product.productId)
                                              : [...newOrder.productIds, product.productId]
                                          });
                                        }}
                                      >
                                        <Check
                                          className={cn(
                                            "mr-2 h-4 w-4",
                                            newOrder.productIds.includes(product.productId) ? "opacity-100" : "opacity-0"
                                          )}
                                        />
                                        <div>
                                          <div className="font-medium">{product.name}</div>
                                          <div className="text-sm text-muted-foreground">
                                            ID: {product.productId} | ${product.price} | Stock: {product.stock}
                                          </div>
                                        </div>
                                      </CommandItem>
                                    ))}
                                  </CommandGroup>
                                </CommandList>
                              </Command>
                            </PopoverContent>
                          </Popover>
                          {newOrder.productIds.length > 0 && (
                            <div className="mt-2 flex flex-wrap gap-2">
                              {newOrder.productIds.map(id => {
                                const product = products.find(p => p.productId === id);
                                return product ? (
                                  <Badge key={id} variant="secondary" className="flex items-center gap-1">
                                    {product.name}
                                    <X
                                      className="h-3 w-3 cursor-pointer"
                                      onClick={() => setNewOrder({
                                        ...newOrder,
                                        productIds: newOrder.productIds.filter(pid => pid !== id)
                                      })}
                                    />
                                  </Badge>
                                ) : null;
                              })}
                            </div>
                          )}
                        </div>
                        <Button onClick={handlePlaceOrder} className="w-full" disabled={!newOrder.customerId || newOrder.productIds.length === 0}>
                          Place Order
                        </Button>
                      </div>
                    </DialogContent>
                  </Dialog>

		  <div className="flex gap-2 flex-1">
		    <Popover open={openOrderCombobox} onOpenChange={setOpenOrderCombobox}>
		      <PopoverTrigger asChild>
			<Button variant="outline" className="flex-1 justify-between">
			  {searchOrderId ? `Order #${searchOrderId}` : "Search by ID..."}
			  <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
			</Button>
		      </PopoverTrigger>
		      <PopoverContent className="w-[400px] p-0">
			<Command>
			  <CommandInput placeholder="Search order ID..." />
			  <CommandList>
			    <CommandEmpty>No order found.</CommandEmpty>
			    <CommandGroup>
			      {orders.map((order) => (
			      <CommandItem
				key={order.orderId}
				value={`${order.orderId} ${order.totalPrice} ${order.status}`} // Value for semantic search/filtering
				onSelect={() => {
				setSearchOrderId(order.orderId.toString());
				setOpenOrderCombobox(false);
				}}
				>
				<Check
				  className={cn(
				  "mr-2 h-4 w-4",
				  searchOrderId === order.orderId.toString() ? "opacity-100" : "opacity-0"
				  )}
				  />
				<div className="flex-1">
				  <div className="font-medium">Order #{order.orderId}</div>
				  <div className="text-sm text-muted-foreground">
				    Status: {order.status} | Total: ${order.totalPrice.toFixed(2)}
				  </div>
				</div>
			      </CommandItem>
			      ))}
			    </CommandGroup>
			  </CommandList>
			</Command>
		      </PopoverContent>
		    </Popover>
		    <Button onClick={handleSearchOrder} variant="secondary">
		      <Search className="h-4 w-4" />
		    </Button>
		  </div>
		</div>

                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Order ID</TableHead>
                      <TableHead>Customer</TableHead>
                      <TableHead>Products</TableHead>
                      <TableHead>Total</TableHead>
                      <TableHead>Date</TableHead>
                      <TableHead>Status</TableHead>
                      <TableHead>Actions</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {orders.map((order) => {
                      const customer = customers.find(c => c.customerId === order.customerId);
                      return (
                        <TableRow key={order.orderId}>
                          <TableCell>{order.orderId}</TableCell>
                          <TableCell>{customer?.name || `Customer #${order.customerId}`}</TableCell>
                          <TableCell>{order.productIds.length} items</TableCell>
                          <TableCell>${order.totalPrice.toFixed(2)}</TableCell>
                          <TableCell>{new Date(order.orderDate).toLocaleDateString()}</TableCell>
                          <TableCell>{getStatusBadge(order.status)}</TableCell>
			  <TableCell>
			    <Select
			      value={order.status}
			      onValueChange={(newStatus) => handleUpdateOrderStatus(order.orderId, newStatus)}
			      >
			      <SelectTrigger className="w-[120px]">
				<SelectValue placeholder={order.status} /> 
			      </SelectTrigger>
			      <SelectContent>
				{['Pending', 'Shipped', 'Delivered'].map(status => (
				<SelectItem key={status} value={status}>
				  {status}
				</SelectItem>
				))}
			      </SelectContent>
			    </Select>
			  </TableCell>
			  
                          <TableCell>
                            {order.status === 'Pending' && (
                            <Button
                              size="sm"
                              variant="destructive"
                              onClick={() => handleCancelOrder(order.orderId)}
                              >
                              Cancel
                            </Button>
                            )}
                          </TableCell>
                        </TableRow>
                      );
                    })}
                  </TableBody>
                </Table>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="reviews" className="space-y-4">
            <Card>
              <CardHeader>
                <CardTitle>Review Management</CardTitle>
                <CardDescription>Customer feedback and ratings</CardDescription>
              </CardHeader>
              <CardContent>
                <Dialog>
                  <DialogTrigger asChild>
                    <Button className="mb-4">Add New Review</Button>
                  </DialogTrigger>
                  <DialogContent>
                    <DialogHeader>
                      <DialogTitle>Add New Review</DialogTitle>
                      <DialogDescription>Select product and customer, then rate</DialogDescription>
                    </DialogHeader>
                    <div className="space-y-4">
                      <div>
                        <Label>Product</Label>
                        <Popover open={openReviewProductCombobox} onOpenChange={setOpenReviewProductCombobox}>
                          <PopoverTrigger asChild>
                            <Button variant="outline" className="w-full justify-between">
                              {newReview.productId ? 
                                products.find(p => p.productId === newReview.productId)?.name || "Select product..." 
                                : "Select product..."}
                              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                            </Button>
                          </PopoverTrigger>
                          <PopoverContent className="w-full p-0">
                            <Command>
                              <CommandInput placeholder="Search product..." />
                              <CommandList>
                                <CommandEmpty>No product found.</CommandEmpty>
                                <CommandGroup>
                                  {products.map((product) => (
                                    <CommandItem
                                      key={product.productId}
                                      value={`${product.productId} ${product.name}`}
                                      onSelect={() => {
                                        setNewReview({ ...newReview, productId: product.productId });
                                        setOpenReviewProductCombobox(false);
                                      }}
                                    >
                                      <Check
                                        className={cn(
                                          "mr-2 h-4 w-4",
                                          newReview.productId === product.productId ? "opacity-100" : "opacity-0"
                                        )}
                                      />
                                      <div>
                                        <div className="font-medium">{product.name}</div>
                                        <div className="text-sm text-muted-foreground">ID: {product.productId} | ${product.price}</div>
                                      </div>
                                    </CommandItem>
                                  ))}
                                </CommandGroup>
                              </CommandList>
                            </Command>
                          </PopoverContent>
                        </Popover>
                      </div>
                      <div>
                        <Label>Customer</Label>
                        <Popover open={openReviewCustomerCombobox} onOpenChange={setOpenReviewCustomerCombobox}>
                          <PopoverTrigger asChild>
                            <Button variant="outline" className="w-full justify-between">
                              {newReview.customerId ? 
                                customers.find(c => c.customerId === newReview.customerId)?.name || "Select customer..." 
                                : "Select customer..."}
                              <ChevronsUpDown className="ml-2 h-4 w-4 shrink-0 opacity-50" />
                            </Button>
                          </PopoverTrigger>
                          <PopoverContent className="w-full p-0">
                            <Command>
                              <CommandInput placeholder="Search customer..." />
                              <CommandList>
                                <CommandEmpty>No customer found.</CommandEmpty>
                                <CommandGroup>
                                  {customers.map((customer) => (
                                    <CommandItem
                                      key={customer.customerId}
                                      value={`${customer.customerId} ${customer.name}`}
                                      onSelect={() => {
                                        setNewReview({ ...newReview, customerId: customer.customerId });
                                        setOpenReviewCustomerCombobox(false);
                                      }}
                                    >
                                      <Check
                                        className={cn(
                                          "mr-2 h-4 w-4",
                                          newReview.customerId === customer.customerId ? "opacity-100" : "opacity-0"
                                        )}
                                      />
                                      <div>
                                        <div className="font-medium">{customer.name}</div>
                                        <div className="text-sm text-muted-foreground">ID: {customer.customerId} | {customer.email}</div>
                                      </div>
                                    </CommandItem>
                                  ))}
                                </CommandGroup>
                              </CommandList>
                            </Command>
                          </PopoverContent>
                        </Popover>
                      </div>
                      <div>
                        <Label>Rating</Label>
                        <div className="flex items-center gap-2 mt-2">
                          {[1, 2, 3, 4, 5].map((star) => (
                            <button
                              key={star}
                              type="button"
                              onClick={() => setNewReview({ ...newReview, rating: star })}
                              onMouseEnter={() => setHoveredStar(star)}
                              onMouseLeave={() => setHoveredStar(null)}
                              className="transition-transform hover:scale-110 focus:outline-none"
                            >
                              <Star
                                className={`h-8 w-8 cursor-pointer transition-colors ${
                                  star <= (hoveredStar || newReview.rating)
                                    ? 'fill-yellow-400 text-yellow-400'
                                    : 'text-gray-300'
                                }`}
                              />
                            </button>
                          ))}
                          <span className="ml-2 text-sm text-slate-600">
                            {newReview.rating} {newReview.rating === 1 ? 'star' : 'stars'}
                          </span>
                        </div>
                      </div>
                      <div>
                        <Label>Comment</Label>
                        <Input
                          value={newReview.comment}
                          onChange={(e) => setNewReview({ ...newReview, comment: e.target.value })}
                          placeholder="Enter review comment"
                        />
                      </div>
                      <Button onClick={handleAddReview} className="w-full" disabled={!newReview.productId || !newReview.customerId}>
                        Add Review
                      </Button>
                    </div>
                  </DialogContent>
                </Dialog>

                <div className="space-y-4">
                  {products.flatMap(product =>
                    product.reviews.map(review => (
                      <Card key={review.reviewId} className="border-l-4 border-l-yellow-400">
                        <CardHeader>
                          <div className="flex items-center justify-between">
                            <CardTitle className="text-base">{product.name}</CardTitle>
                            <div className="flex items-center gap-2">
                              <div className="flex items-center gap-1">
                                {[...Array(5)].map((_, i) => (
                                  <Star
                                    key={i}
                                    className={`h-4 w-4 ${
                                      i < review.rating ? 'fill-yellow-400 text-yellow-400' : 'text-gray-300'
                                    }`}
                                  />
                                ))}
                              </div>
                              <Button
                                size="sm"
                                variant="outline"
                                onClick={() => setEditingReview({ review, product })}
                              >
                                <Edit className="h-4 w-4" />
                              </Button>
                            </div>
                          </div>
                          <CardDescription>
                            Customer: {customers.find(c => c.customerId === review.customerId)?.name || `#${review.customerId}`}
                          </CardDescription>
                        </CardHeader>
                        <CardContent>
                          <p className="text-sm text-slate-700">{review.comment}</p>
                        </CardContent>
                      </Card>
                    ))
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>

      {editingProduct && (
        <Dialog open={!!editingProduct} onOpenChange={() => setEditingProduct(null)}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Edit Product</DialogTitle>
              <DialogDescription>Update product details</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label>Product Name</Label>
                <Input
                  value={editingProduct.name}
                  onChange={(e) => setEditingProduct({ ...editingProduct, name: e.target.value })}
                />
              </div>
              <div>
                <Label>Price</Label>
                <Input
                  type="number"
                  step="0.01"
                  value={editingProduct.price}
                  onChange={(e) => setEditingProduct({ ...editingProduct, price: parseFloat(e.target.value) })}
                />
              </div>
              <div>
                <Label>Stock</Label>
                <Input
                  type="number"
                  value={editingProduct.stock}
                  onChange={(e) => setEditingProduct({ ...editingProduct, stock: parseInt(e.target.value) })}
                />
              </div>
              <Button onClick={handleUpdateProduct} className="w-full">Update Product</Button>
            </div>
          </DialogContent>
        </Dialog>
      )}

      {editingReview && (
        <Dialog open={!!editingReview} onOpenChange={() => setEditingReview(null)}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Edit Review</DialogTitle>
              <DialogDescription>Update review for {editingReview.product.name}</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label>Rating</Label>
                <div className="flex items-center gap-2 mt-2">
                  {[1, 2, 3, 4, 5].map((star) => (
                    <button
                      key={star}
                      type="button"
                      onClick={() => setEditingReview({
                        ...editingReview,
                        review: { ...editingReview.review, rating: star }
                      })}
                      onMouseEnter={() => setHoveredStar(star)}
                      onMouseLeave={() => setHoveredStar(null)}
                      className="transition-transform hover:scale-110 focus:outline-none"
                    >
                      <Star
                        className={`h-8 w-8 cursor-pointer transition-colors ${
                          star <= (hoveredStar || editingReview.review.rating)
                            ? 'fill-yellow-400 text-yellow-400'
                            : 'text-gray-300'
                        }`}
                      />
                    </button>
                  ))}
                  <span className="ml-2 text-sm text-slate-600">
                    {editingReview.review.rating} {editingReview.review.rating === 1 ? 'star' : 'stars'}
                  </span>
                </div>
              </div>
              <div>
                <Label>Comment</Label>
                <Input
                  value={editingReview.review.comment}
                  onChange={(e) => setEditingReview({
                    ...editingReview,
                    review: { ...editingReview.review, comment: e.target.value }
                  })}
                />
              </div>
              <Button onClick={handleEditReview} className="w-full">Update Review</Button>
            </div>
          </DialogContent>
        </Dialog>
      )}

      {selectedCustomerHistory && (
        <Dialog open={!!selectedCustomerHistory} onOpenChange={() => setSelectedCustomerHistory(null)}>
          <DialogContent className="max-w-3xl">
            <DialogHeader>
              <DialogTitle>Order History - {selectedCustomerHistory.name}</DialogTitle>
              <DialogDescription>{selectedCustomerHistory.email}</DialogDescription>
            </DialogHeader>
            <div className="space-y-4">
              {customerOrders.length === 0 ? (
                <p className="text-center text-muted-foreground py-8">No orders found</p>
              ) : (
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Order ID</TableHead>
                      <TableHead>Products</TableHead>
                      <TableHead>Total</TableHead>
                      <TableHead>Date</TableHead>
                      <TableHead>Status</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {customerOrders.map((order) => (
                      <TableRow key={order.orderId}>
                        <TableCell>{order.orderId}</TableCell>
                        <TableCell>{order.productIds.length} items</TableCell>
                        <TableCell>${order.totalPrice.toFixed(2)}</TableCell>
                        <TableCell>{new Date(order.orderDate).toLocaleDateString()}</TableCell>
                        <TableCell>{getStatusBadge(order.status)}</TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              )}
            </div>
          </DialogContent>
        </Dialog>
      )}
    </div>
  );
}
