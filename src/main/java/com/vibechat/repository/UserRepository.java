package com.vibechat.repository;

import com.vibechat.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Search users by username (case-insensitive partial match)
     * Used for user search functionality
     */
    List<User> findByUsernameContainingIgnoreCase(String searchTerm);
}
