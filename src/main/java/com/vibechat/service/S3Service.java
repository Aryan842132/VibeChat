package com.vibechat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    // Allowed image types
    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "image/jpeg",
            "image/png",
            "image/gif",
            "image/webp"
    );

    // Max file size: 5MB
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * Upload profile picture to S3 and return URL
     */
    public String uploadProfilePicture(MultipartFile file) throws IOException {
        // Validate file
        validateFile(file);

        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        String fileName = UUID.randomUUID().toString() + extension;
        String key = "profile-pictures/" + fileName;

        // Upload to S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(
                file.getInputStream(),
                file.getSize()
        ));

        // Return public URL
        return getS3ObjectUrl(key);
    }

    /**
     * Delete profile picture from S3
     */
    public void deleteProfilePicture(String imageUrl) {
        try {
            // Extract key from URL
            String key = extractKeyFromUrl(imageUrl);
            if (key != null) {
                DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(key)
                        .build();
                s3Client.deleteObject(deleteRequest);
                log.info("Deleted S3 object: {}", key);
            }
        } catch (Exception e) {
            log.error("Error deleting S3 object: {}", e.getMessage());
        }
    }

    /**
     * Get pre-signed URL for direct upload (optional advanced usage)
     */
    public String getPresignedUploadUrl(String fileName) {
        String key = "profile-pictures/" + fileName;

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(15))
                .putObjectRequest(builder -> builder
                        .bucket(bucketName)
                        .key(key)
                        .contentType("image/jpeg")
                )
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
        return presignedRequest.url().toString();
    }

    /**
     * Validate uploaded file
     */
    private void validateFile(MultipartFile file) throws IOException {
        // Check if file is empty
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Check file size
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        // Check content type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new IllegalArgumentException("Only JPEG, PNG, GIF, and WebP images are allowed");
        }
    }

    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Default extension
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * Get public URL for S3 object
     */
    private String getS3ObjectUrl(String key) {
        return String.format("https://%s.s3.%s.amazonaws.com/%s",
                bucketName,
                s3Client.serviceClientConfiguration().region().id(),
                key);
    }

    /**
     * Extract S3 key from URL
     */
    private String extractKeyFromUrl(String imageUrl) {
        try {
            String urlPrefix = String.format("https://%s.s3.%s.amazonaws.com/",
                    bucketName,
                    s3Client.serviceClientConfiguration().region().id());

            if (imageUrl.startsWith(urlPrefix)) {
                return imageUrl.substring(urlPrefix.length());
            }
        } catch (Exception e) {
            log.error("Error extracting key from URL: {}", e.getMessage());
        }
        return null;
    }
}
