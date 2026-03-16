package com.vibechat.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;
    
    @Indexed
    private List<String> participants;

    private ChatMessage lastMessage;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isParticipant(String userId) {
        return participants != null && participants.contains(userId);
    }

    public String getOtherParticipant(String currentUserId) {
        if (participants == null || participants.size() != 2) {
            return null;
        }
        return participants.stream()
                .filter(id -> !id.equals(currentUserId))
                .findFirst()
                .orElse(null);
    }
}
