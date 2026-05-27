package com.vibechat.repository;

import com.vibechat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    
    boolean existsByEmail(String email);

    
    boolean existsByUsername(String username);

    
    List<User> findByUsernameContainingIgnoreCase(String searchTerm);
}
