package com.inventory.repository;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.Inventory;

public interface InventoryRepo extends MongoRepository<Inventory, String> {
    Optional<Inventory> findByProductId(String productId);
}
