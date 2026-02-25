package com.inventory.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.inventory.entity.DatabaseSequence;

public interface DatabaseSequenceRepository extends MongoRepository<DatabaseSequence, String> {
}
