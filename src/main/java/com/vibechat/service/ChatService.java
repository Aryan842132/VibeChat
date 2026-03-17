package com.vibechat.service;

import com.vibechat.model.ChatMessage;
import com.vibechat.model.User;
import com.vibechat.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ConversationService conversationService;

    public ChatMessage sendMessage(String senderId, String receiverId, String content, 
                                   ChatMessage.MessageType messageType) {
 
        ChatMessage message = ChatMessage.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .messageType(messageType)
                .status(ChatMessage.MessageStatus.SENT)
                .timestamp(LocalDateTime.now())
                .build();

        // Save message to database
        ChatMessage savedMessage = messageRepository.save(message);
        log.info("Message saved: {}", savedMessage.getId());

        // Create or update conversation automatically
        conversationService.updateLastMessage(senderId, receiverId, savedMessage);

        // Send to receiver via WebSocket - using explicit destination path
        log.info("Sending message to user {}: {}", receiverId, savedMessage.getId());
        messagingTemplate.convertAndSend(
            "/user/" + receiverId + "/queue/messages", 
            savedMessage
        );
        log.info("Message sent to /user/{}/queue/messages", receiverId);

        // Broadcast to public topic for all subscribers
        messagingTemplate.convertAndSend("/topic/messages", savedMessage);
        log.info("Message broadcasted to /topic/messages");

        return savedMessage;
    }

    public ChatMessage updateMessageStatus(String messageId, ChatMessage.MessageStatus status) {
        ChatMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));

        message.setStatus(status);
        ChatMessage updatedMessage = messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
            message.getSenderId(),
            "/queue/status",
            updatedMessage
        );

        return updatedMessage;
    }

    public Iterable<ChatMessage> getChatHistory(String userId1, String userId2) {
        return messageRepository.findBySenderIdOrReceiverIdOrderByTimestampDesc(userId1, userId2);
    }

    public void markMessagesAsRead(String senderId, String receiverId) {
        log.info("Marking messages from {} to {} as read", senderId, receiverId);
    }
}
