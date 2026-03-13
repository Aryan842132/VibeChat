package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.model.ChatMessage;
import com.vibechat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;


@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    @SendTo("/topic/messages")
    public ApiResponse<ChatMessage> sendMessage(@Payload ChatMessage message) {
        log.info("Received message: from={} to={} content={}", 
                message.getSenderId(), message.getReceiverId(), message.getContent());

        ChatMessage savedMessage = chatService.sendMessage(
            message.getSenderId(),
            message.getReceiverId(),
            message.getContent(),
            message.getMessageType() != null ? message.getMessageType() : ChatMessage.MessageType.TEXT
        );

        return ApiResponse.success("Message sent", savedMessage);
    }

    @MessageMapping("/chat.private.{userId}")
    public void sendPrivateMessage(
            @DestinationVariable String userId,
            @Payload ChatMessage message) {
        
        log.info("Private message to {}: {}", userId, message.getContent());

        ChatMessage savedMessage = chatService.sendMessage(
            message.getSenderId(),
            userId,
            message.getContent(),
            message.getMessageType() != null ? message.getMessageType() : ChatMessage.MessageType.TEXT
        );

        messagingTemplate.convertAndSendToUser(userId, "/queue/private-message", savedMessage);
    }

    @MessageMapping("/chat.status")
    public ApiResponse<ChatMessage> updateMessageStatus(@Payload MessageStatusUpdate statusUpdate) {
        log.info("Updating message {} status to {}", statusUpdate.getMessageId(), statusUpdate.getStatus());

        ChatMessage updatedMessage = chatService.updateMessageStatus(
            statusUpdate.getMessageId(),
            statusUpdate.getStatus()
        );

        return ApiResponse.success("Status updated", updatedMessage);
    }

    
    @GetMapping("/api/chat/history/{userId1}/{userId2}")
    public ApiResponse<Iterable<ChatMessage>> getChatHistory(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        
        Iterable<ChatMessage> messages = chatService.getChatHistory(userId1, userId2);
        return ApiResponse.success("Chat history retrieved", messages);
    }

    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MessageStatusUpdate {
        private String messageId;
        private ChatMessage.MessageStatus status;
    }
}
