package com.inventory.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.StockMovement;

public interface StockMovementRepo extends MongoRepository<StockMovement, String> {
    List<StockMovement> findByProductId(String productId);
}
