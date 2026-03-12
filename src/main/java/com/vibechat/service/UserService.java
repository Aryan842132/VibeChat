package com.vibechat.service;

import com.vibechat.dto.AuthResponse;
import com.vibechat.dto.LoginRequest;
import com.vibechat.dto.RegisterRequest;
import com.vibechat.dto.UserResponse;
import com.vibechat.model.User;
import com.vibechat.repository.UserRepository;
import com.vibechat.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final S3Service s3service;

    
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(User.UserStatus.OFFLINE);
        user.setLastSeen(LocalDateTime.now());
        user.setCreatedAt(LocalDateTime.now());

        
        User savedUser = userRepository.save(user);

        
        String token = jwtTokenProvider.generateToken(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getUsername()
        );

        
        UserResponse userResponse = UserResponse.fromEntity(savedUser);
        return AuthResponse.fromUserResponseWithToken(userResponse, token);
    }

    
    public AuthResponse login(LoginRequest request) {
        
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        
        user.setStatus(User.UserStatus.ONLINE);
        user.setLastSeen(LocalDateTime.now());
        userRepository.save(user);

        
        String token = jwtTokenProvider.generateToken(
            user.getId(),
            user.getEmail(),
            user.getUsername()
        );

        
        UserResponse userResponse = UserResponse.fromEntity(user);
        return AuthResponse.fromUserResponseWithToken(userResponse, token);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return UserResponse.fromEntity(user);
    }

    
    public UserResponse updateProfilePicture(String userId, String profilePictureUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getProfilePicture() != null) {
            s3service.deleteProfilePicture(user.getProfilePicture());
        }

        
        user.setProfilePicture(profilePictureUrl);
        userRepository.save(user);

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
