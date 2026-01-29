package com.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.PurchaseOrder;

public interface PurchaseOrderRepo extends MongoRepository<PurchaseOrder, String> {
}
