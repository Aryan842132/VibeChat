package com.vibechat.repository;

import com.vibechat.model.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    
    
    Optional<Conversation> findByParticipantsContainingAndParticipantsContaining(
        String userId1, 
        String userId2
    );
    
    
    List<Conversation> findByParticipantsContainingOrderByUpdatedAtDesc(String userId);
    
    
    boolean existsByParticipantsContainingAndParticipantsContaining(
        String userId1, 
        String userId2
    );
}
