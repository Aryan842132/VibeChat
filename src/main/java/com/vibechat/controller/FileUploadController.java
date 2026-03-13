package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.dto.FileUploadResponse;
import com.vibechat.service.S3Service;
import com.vibechat.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final S3Service s3Service;
    private final UserService userService;

    @PostMapping("/profile-picture")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "userId", required = false) String userId) {
        
        try {
            
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("File cannot be empty")
                );
            }

            String fileUrl = s3Service.uploadProfilePicture(file);

            // If userId is provided, update the database
            if (userId != null && !userId.isEmpty()) {
                userService.updateProfilePicture(userId, fileUrl);
            }

            FileUploadResponse response = FileUploadResponse.builder()
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .message("Profile picture uploaded successfully" + 
                            (userId != null ? " and saved to database" : ""))
                    .build();

            return ResponseEntity.ok(ApiResponse.success("Upload successful", response));

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

    @GetMapping("/presigned-url")
    public ResponseEntity<ApiResponse<String>> getPresignedUploadUrl(
            @RequestParam("fileName") String fileName) {
        
        try {
            String presignedUrl = s3Service.getPresignedUploadUrl(fileName);
            return ResponseEntity.ok(ApiResponse.success("Pre-signed URL generated", presignedUrl));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to generate URL: " + e.getMessage())
            );
        }
    }
}
