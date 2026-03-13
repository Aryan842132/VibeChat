package com.vibechat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Chat message entity for real-time messaging
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;

    private String senderId;

    private String receiverId;

    private String content;

    private MessageType messageType;

    private MessageStatus status;

    @CreatedDate
    private LocalDateTime timestamp;

    public enum MessageType {
        TEXT,
        IMAGE,
        VIDEO
    }

    public enum MessageStatus {
        SENT,
        DELIVERED,
        READ
    }
}
