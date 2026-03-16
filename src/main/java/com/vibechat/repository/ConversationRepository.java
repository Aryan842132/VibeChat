package com.vibechat.repository;

import com.vibechat.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for conversation operations
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    
    /**
     * Find conversation between two users (bidirectional search)
     * Looks for conversations where both users are participants
     */
    Optional<Conversation> findByParticipantsContainingAndParticipantsContaining(
        String userId1, 
        String userId2
    );
    
    /**
     * Find all conversations for a specific user
     * Returns conversations sorted by updatedAt descending (most recent first)
     */
    List<Conversation> findByParticipantsContainingOrderByUpdatedAtDesc(String userId);
    
    /**
     * Check if a conversation exists between two users
     */
    boolean existsByParticipantsContainingAndParticipantsContaining(
        String userId1, 
        String userId2
    );
}
