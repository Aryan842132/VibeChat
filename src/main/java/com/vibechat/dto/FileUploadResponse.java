package com.vibechat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for file upload response
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileUploadResponse {

    private String fileName;

    private String fileUrl;

    private String fileType;

    private long fileSize;

    private String message;
}
