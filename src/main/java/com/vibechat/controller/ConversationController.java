package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.model.Conversation;
import com.vibechat.service.ConversationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * REST controller for conversation management
 */
@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConversationController {

    private final ConversationService conversationService;

    /**
     * Create a new conversation between users
     * POST /api/conversations/create
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<Conversation>> createConversation(
            @RequestBody CreateConversationRequest request) {
        
        try {
            if (request.getParticipants() == null || request.getParticipants().size() < 2) {
                return ResponseEntity.badRequest().body(
                    ApiResponse.error("Conversation must have at least 2 participants")
                );
            }

            Conversation conversation = conversationService.createConversation(
                request.getParticipants()
            );

            return ResponseEntity.ok(ApiResponse.success(
                "Conversation created successfully", 
                conversation
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error(e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to create conversation: " + e.getMessage())
            );
        }
    }

    /**
     * Get all conversations for a specific user
     * GET /api/conversations/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Conversation>>> getUserConversations(
            @PathVariable String userId) {
        
        try {
            List<Conversation> conversations = conversationService.getUserConversations(userId);

            return ResponseEntity.ok(ApiResponse.success(
                "Conversations retrieved successfully", 
                conversations
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to retrieve conversations: " + e.getMessage())
            );
        }
    }

    /**
     * Get conversation by ID
     * GET /api/conversations/{conversationId}
     */
    @GetMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Conversation>> getConversationById(
            @PathVariable String conversationId) {
        
        try {
            Conversation conversation = conversationService.getConversationById(conversationId);

            return ResponseEntity.ok(ApiResponse.success(
                "Conversation retrieved successfully", 
                conversation
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to retrieve conversation: " + e.getMessage())
            );
        }
    }

    /**
     * Find or create conversation between two users
     * GET /api/conversations/between/{userId1}/{userId2}
     */
    @GetMapping("/between/{userId1}/{userId2}")
    public ResponseEntity<ApiResponse<Conversation>> findOrCreateConversation(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        
        try {
            Conversation conversation = conversationService.findOrCreateConversation(
                userId1, userId2
            );

            return ResponseEntity.ok(ApiResponse.success(
                "Conversation found/created successfully", 
                conversation
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to find/create conversation: " + e.getMessage())
            );
        }
    }

    /**
     * Delete conversation
     * DELETE /api/conversations/{conversationId}
     */
    @DeleteMapping("/{conversationId}")
    public ResponseEntity<ApiResponse<Void>> deleteConversation(
            @PathVariable String conversationId) {
        
        try {
            conversationService.deleteConversation(conversationId);

            return ResponseEntity.ok(ApiResponse.success(
                "Conversation deleted successfully", 
                null
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Failed to delete conversation: " + e.getMessage())
            );
        }
    }

    /**
     * Request DTO for creating conversations
     */
    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    public static class CreateConversationRequest {
        private List<String> participants;
    }
}
