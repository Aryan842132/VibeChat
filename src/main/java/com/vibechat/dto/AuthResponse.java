package com.vibechat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication response containing user info and JWT token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String id;

    private String username;

    private String email;

    private String profilePicture;

    private String status;

    private String token;

    public static AuthResponse fromUserResponseWithToken(UserResponse user, String token) {
        return AuthResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .status(user.getStatus())
                .token(token)
                .build();
    }
}
