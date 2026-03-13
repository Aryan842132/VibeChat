package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.dto.UserResponse;
import com.vibechat.service.S3Service;
import com.vibechat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserService userService;
    private final S3Service s3Service;

    
    @PostMapping("/{userId}/upload-profile-picture")
    public ResponseEntity<ApiResponse<UserResponse>> uploadAndUpdateProfilePicture(
            @PathVariable String userId,
            @RequestParam("file") MultipartFile file) throws IOException {
        
        try {
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("File cannot be empty")
                );
            }


            String imageUrl = s3Service.uploadProfilePicture(file);

            
            UserResponse updatedUser = userService.updateProfilePicture(userId, imageUrl);

            return ResponseEntity.ok(ApiResponse.success(
                "Profile picture updated successfully", 
                updatedUser
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Upload failed: " + e.getMessage())
            );
        }
    }

    
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
