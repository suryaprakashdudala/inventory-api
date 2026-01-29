package com.inventory.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.Product;

public interface ProductRepo extends MongoRepository<Product, String> {
    Optional<Product> findBySku(String sku);
}
