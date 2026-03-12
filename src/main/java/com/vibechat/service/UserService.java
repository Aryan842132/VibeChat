package com.vibechat.service;

import com.vibechat.dto.LoginRequest;
import com.vibechat.dto.RegisterRequest;
import com.vibechat.dto.UserResponse;
import com.vibechat.model.User;
import com.vibechat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

       public UserResponse register(RegisterRequest request) {
        // Validate email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Validate username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(User.UserStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        // Save user
        User savedUser = userRepository.save(user);

        return UserResponse.fromEntity(savedUser);
    }

    /**
     * Authenticate user and return user info
     */
    public UserResponse login(LoginRequest request) {
        // Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Update status and last seen
        user.setStatus(User.UserStatus.ONLINE);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.fromEntity(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.fromEntity(user);
    }

    public void updateUserStatus(String userId, User.UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setStatus(status);
        if (status == User.UserStatus.OFFLINE) {
            user.setLastSeen(LocalDateTime.now());
        }

        userRepository.save(user);
    }

    public void logout(String userId) {
        updateUserStatus(userId, User.UserStatus.OFFLINE);
    }
}
