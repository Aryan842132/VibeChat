package com.vibechat.repository;

import com.vibechat.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends MongoRepository<ChatMessage, String> {
    
    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(String senderId, String receiverId);
    
    List<ChatMessage> findBySenderIdOrderByTimestampDesc(String senderId);
    
    List<ChatMessage> findByReceiverIdOrderByTimestampDesc(String receiverId);
    
    List<ChatMessage> findBySenderIdOrReceiverIdOrderByTimestampDesc(String senderId, String receiverId);
}
