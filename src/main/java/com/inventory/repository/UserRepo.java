package com.inventory.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.inventory.entity.User;

public interface UserRepo extends MongoRepository<User, String> {
    Optional<User> findByUserName(String userName);
    
    Optional<User> findByEmail(String email);

    java.util.List<User> findAllByRole(com.inventory.entity.Role role);
}
