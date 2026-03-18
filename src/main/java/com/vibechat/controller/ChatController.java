package com.vibechat.controller;

import com.vibechat.model.ChatMessage;
import com.vibechat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage message) {

        log.info("PUBLIC: from={} content={}",
                message.getSenderId(),
                message.getContent());

        ChatMessage savedMessage = chatService.sendMessage(
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getMessageType() != null
                        ? message.getMessageType()
                        : ChatMessage.MessageType.TEXT
        );

        messagingTemplate.convertAndSend("/topic/messages", savedMessage);
    }

    @MessageMapping("/chat.private")
    public void sendPrivateMessage(@Payload ChatMessage message) {

        log.info("PRIVATE: from={} to={} content={}",
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent());

        ChatMessage savedMessage = chatService.sendMessage(
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getMessageType() != null
                        ? message.getMessageType()
                        : ChatMessage.MessageType.TEXT
        );

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/private",
                savedMessage
        );

        messagingTemplate.convertAndSendToUser(
                message.getSenderId(),
                "/queue/private",
                savedMessage
        );
    }

    @MessageMapping("/chat.status")
    public void updateMessageStatus(@Payload MessageStatusUpdate statusUpdate) {

        log.info("STATUS: messageId={} status={}",
                statusUpdate.getMessageId(),
                statusUpdate.getStatus());

        ChatMessage updatedMessage = chatService.updateMessageStatus(
                statusUpdate.getMessageId(),
                statusUpdate.getStatus()
        );

        messagingTemplate.convertAndSendToUser(
                updatedMessage.getSenderId(),
                "/queue/status",
                updatedMessage
        );
    }
    
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class MessageStatusUpdate {
        private String messageId;
        private ChatMessage.MessageStatus status;
    }
}