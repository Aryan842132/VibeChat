package com.vibechat.dto;

import com.vibechat.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for user response - does not expose sensitive data like password
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private String id;

    private String username;

    private String email;

    private String profilePicture;

    private String status;

    private LocalDateTime lastSeen;

    private LocalDateTime createdAt;

    /**
     * Creates UserResponse from User entity
     */
    public static UserResponse fromEntity(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .status(user.getStatus() != null ? user.getStatus().name() : User.UserStatus.OFFLINE.name())
                .lastSeen(user.getLastSeen())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
