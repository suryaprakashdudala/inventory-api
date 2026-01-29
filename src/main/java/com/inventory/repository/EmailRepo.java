package com.inventory.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.inventory.entity.EmailQueue;

public interface EmailRepo extends MongoRepository<EmailQueue, String> {
	
    Optional<EmailQueue> findTopByEmailAndUsedFalseOrderByCreatedAtDesc(String email);
    
    void deleteByExpiresAtBefore(LocalDateTime time);
    void deleteByUsedTrue();
}
