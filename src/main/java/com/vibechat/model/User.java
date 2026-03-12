package com.vibechat.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * User entity representing a chat application user
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String password;

    private String profilePicture;

    private UserStatus status;

    private LocalDateTime lastSeen;

    @CreatedDate
    private LocalDateTime createdAt;

    /**
     * User status enum for tracking online/offline state
     */
    public enum UserStatus {
        ONLINE,
        OFFLINE
    }
}
