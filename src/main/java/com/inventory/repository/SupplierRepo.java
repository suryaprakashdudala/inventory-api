package com.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.Supplier;

public interface SupplierRepo extends MongoRepository<Supplier, String> {
}
