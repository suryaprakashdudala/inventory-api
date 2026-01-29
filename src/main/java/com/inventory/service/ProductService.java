package com.inventory.service;

import java.util.List;
import java.util.Optional;
import com.inventory.entity.Product;

public interface ProductService {
    Product addProduct(Product product);
    Product updateProduct(String id, Product product);
    Optional<Product> getProductById(String id);
    Optional<Product> getProductBySku(String sku);
    List<Product> getAllProducts();
    void deleteProduct(String id);
}
