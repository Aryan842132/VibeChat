package com.vibechat.controller;

import com.vibechat.dto.ApiResponse;
import com.vibechat.dto.AuthResponse;
import com.vibechat.dto.LoginRequest;
import com.vibechat.dto.RegisterRequest;
import com.vibechat.dto.UserResponse;
import com.vibechat.service.UserService;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

	@Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse authResponse = userService.register(request);
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", authResponse));
    }

    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse authResponse = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User retrieved successfully", user));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<java.util.List<UserResponse>>> searchUsers(
            @RequestParam("query") String query,
            @RequestParam(value = "currentUserId", required = false) String currentUserId) {
        
        try {
            java.util.List<UserResponse> users = userService.searchUsersByName(query, currentUserId);

            return ResponseEntity.ok(ApiResponse.success(
                "Search completed successfully. Found " + users.size() + " user(s).",
                users
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                ApiResponse.error("Search failed: " + e.getMessage())
            );
        }
    }
}
