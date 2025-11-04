package com.ecommerce.utils;

import com.ecommerce.datastructures.ArrayList;
import com.ecommerce.models.*;
import com.ecommerce.services.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utility class for reading CSV files and populating the system
 * Uses BufferedReader for CSV parsing
 */
public class CSVReader {
    
    public static void loadProducts(String filePath, ProductService productService) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 4) {
                    int productId = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    double price = Double.parseDouble(values[2].trim());
                    int stock = Integer.parseInt(values[3].trim());
                    
                    Product product = new Product(productId, name, price, stock);
                    productService.addProduct(product);
                }
            }
            
            System.out.println("Loaded " + productService.getProductCount() + " products from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading products file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing product data: " + e.getMessage());
        }
    }
    
    public static void loadCustomers(String filePath, CustomerService customerService) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = line.split(",");
                if (values.length >= 3) {
                    int customerId = Integer.parseInt(values[0].trim());
                    String name = values[1].trim();
                    String email = values[2].trim();
                    
                    Customer customer = new Customer(customerId, name, email);
                    customerService.registerCustomer(customer);
                }
            }
            
            System.out.println("Loaded " + customerService.getCustomerCount() + " customers from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading customers file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing customer data: " + e.getMessage());
        }
    }
    
    public static void loadOrders(String filePath, OrderService orderService) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = parseCSVLine(line);
                if (values.length >= 6) {
                    int orderId = Integer.parseInt(values[0].trim());
                    int customerId = Integer.parseInt(values[1].trim());
                    
                    String productIdsStr = values[2].trim().replace("\"", "");
                    String[] productIdArray = productIdsStr.split(";");
                    ArrayList<Integer> productIds = new ArrayList<>();
                    for (String pidStr : productIdArray) {
                        productIds.add(Integer.parseInt(pidStr.trim()));
                    }
                    
                    double totalPrice = Double.parseDouble(values[3].trim());
                    String orderDate = values[4].trim();
                    String status = values[5].trim();
                    
                    Order order = new Order(orderId, customerId, productIds, totalPrice, orderDate, status);
                    orderService.createOrder(order);
                }
            }
            
            System.out.println("Loaded " + orderService.getOrderCount() + " orders from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading orders file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing order data: " + e.getMessage());
        }
    }
    
    public static void loadReviews(String filePath, ProductService productService) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                String[] values = parseCSVLine(line);
                if (values.length >= 5) {
                    int reviewId = Integer.parseInt(values[0].trim());
                    int productId = Integer.parseInt(values[1].trim());
                    int customerId = Integer.parseInt(values[2].trim());
                    int rating = Integer.parseInt(values[3].trim());
                    String comment = values[4].trim().replace("\"", "");
                    
                    Review review = new Review(reviewId, productId, customerId, rating, comment);
                    productService.addReviewToProduct(productId, review);
                }
            }
            
            System.out.println("Loaded reviews from " + filePath);
            
        } catch (IOException e) {
            System.err.println("Error reading reviews file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error parsing review data: " + e.getMessage());
        }
    }
    
    private static String[] parseCSVLine(String line) {
        ArrayList<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        
        String[] result = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }
        
        return result;
    }
}
