package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.dto.UserResponse;
import com.vibechat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;

    
    @PutMapping("/{userId}/profile-picture")
    public ResponseEntity<ApiResponse<UserResponse>> updateProfilePicture(
            @PathVariable String userId,
            @RequestBody Map<String, String> body) {
        
        try {
            String profilePictureUrl = body.get("profilePicture");
            
            if (profilePictureUrl == null || profilePictureUrl.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Profile picture URL is required")
                );
            }

            UserResponse updatedUser = userService.updateProfilePicture(userId, profilePictureUrl);

            return ResponseEntity.ok(ApiResponse.success(
                "Profile picture updated successfully", 
                updatedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }

    @DeleteMapping("/{userId}/profile-picture")
    public ResponseEntity<ApiResponse<UserResponse>> removeProfilePicture(
            @PathVariable String userId) {
        
        try {
            UserResponse updatedUser = userService.updateProfilePicture(userId, null);

            return ResponseEntity.ok(ApiResponse.success(
                "Profile picture removed successfully", 
                updatedUser
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        }
    }
}
