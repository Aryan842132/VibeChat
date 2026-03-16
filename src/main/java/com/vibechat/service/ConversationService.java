package com.vibechat.service;

import com.vibechat.model.ChatMessage;
import com.vibechat.model.Conversation;
import com.vibechat.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Conversation createConversation(List<String> participants) {
        if (participants == null || participants.size() < 2) {
            throw new IllegalArgumentException("Conversation must have at least 2 participants");
        }

        List<String> sortedParticipants = participants.stream()
                .sorted()
                .toList();

        Optional<Conversation> existingConversation = findConversationBetweenUsers(
            sortedParticipants.get(0), 
            sortedParticipants.get(1)
        );

        if (existingConversation.isPresent()) {
            log.info("Conversation already exists: {}", existingConversation.get().getId());
            return existingConversation.get();
        }

        Conversation conversation = Conversation.builder()
                .participants(sortedParticipants)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Conversation savedConversation = conversationRepository.save(conversation);
        log.info("Created new conversation: {} between {} participants", 
                savedConversation.getId(), participants.size());

        return savedConversation;
    }

    public Conversation findOrCreateConversation(String userId1, String userId2) {
        return findConversationBetweenUsers(userId1, userId2)
                .orElseGet(() -> {
                    log.info("No conversation found, creating new one between {} and {}", 
                            userId1, userId2);
                    return createConversation(Arrays.asList(userId1, userId2));
                });
    }

    public Optional<Conversation> findConversationBetweenUsers(String userId1, String userId2) {
        return conversationRepository.findByParticipantsContainingAndParticipantsContaining(
            userId1, userId2
        );
    }

    public List<Conversation> getUserConversations(String userId) {
        List<Conversation> conversations = conversationRepository
                .findByParticipantsContainingOrderByUpdatedAtDesc(userId);
        
        log.info("Retrieved {} conversations for user {}", conversations.size(), userId);
        return conversations;
    }

    public Conversation getConversationById(String conversationId) {
        return conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
    }

    public Conversation updateLastMessage(String conversationId, ChatMessage message) {
        Conversation conversation = getConversationById(conversationId);
        
        conversation.setLastMessage(message);
        conversation.setUpdatedAt(LocalDateTime.now());
        
        Conversation updatedConversation = conversationRepository.save(conversation);
        log.debug("Updated last message for conversation: {}", conversationId);
        
        return updatedConversation;
    }

    public Conversation updateLastMessage(String userId1, String userId2, ChatMessage message) {
        Conversation conversation = findOrCreateConversation(userId1, userId2);
        return updateLastMessage(conversation.getId(), message);
    }

    public boolean conversationExists(String userId1, String userId2) {
        return conversationRepository.existsByParticipantsContainingAndParticipantsContaining(
            userId1, userId2
        );
    }

    public void deleteConversation(String conversationId) {
        if (!conversationRepository.existsById(conversationId)) {
            throw new RuntimeException("Conversation not found");
        }
        
        conversationRepository.deleteById(conversationId);
        log.info("Deleted conversation: {}", conversationId);
    }
}
