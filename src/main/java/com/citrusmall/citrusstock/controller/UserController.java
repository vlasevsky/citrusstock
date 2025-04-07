package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.UserCreateRequest;
import com.citrusmall.citrusstock.dto.UserResponse;
import com.citrusmall.citrusstock.mapper.UserMapper;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.service.UserService;
import com.citrusmall.citrusstock.service.RefreshTokenService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/warehouse/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            logger.info("Creating new user with username: {}", request.getUsername());
            User user = userMapper.toUser(request);
            User savedUser = userService.createUser(user);
            UserResponse response = userMapper.toUserResponse(savedUser);
            logger.info("Successfully created user with ID: {}", savedUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error creating user: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        try {
            logger.info("Getting user with ID: {}", id);
            User user = userService.getUserById(id);
            UserResponse response = userMapper.toUserResponse(user);
            logger.info("Successfully retrieved user with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error getting user with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id " + id);
        }
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        try {
            logger.info("Getting all users");
            List<UserResponse> responses = userService.getAllUsers().stream()
                    .map(userMapper::toUserResponse)
                    .collect(Collectors.toList());
            logger.info("Successfully retrieved {} users", responses.size());
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            logger.error("Error getting all users: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error getting users: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserCreateRequest request) {
        try {
            logger.info("Updating user with ID: {}", id);
            User user = userMapper.toUser(request);
            User updatedUser = userService.updateUser(id, user);
            UserResponse response = userMapper.toUserResponse(updatedUser);
            logger.info("Successfully updated user with ID: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error updating user with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error updating user: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            logger.info("Deleting user with ID: {}", id);
            userService.deleteUser(id);
            logger.info("Successfully deleted user with ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error deleting user with ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error deleting user: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> updateUserStatus(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        try {
            logger.info("Updating status for user ID: {} to enabled: {}", id, enabled);
            
            // Get current authenticated user
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !(auth.getPrincipal() instanceof User)) {
                logger.error("Authentication failed: Principal is not a User instance");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
            }
            
            User currentUser = (User) auth.getPrincipal();
            logger.debug("Current user: {}", currentUser.getUsername());
            
            // Check if user is trying to modify their own status
            if (currentUser.getId().equals(id)) {
                logger.warn("User {} attempted to change their own status", currentUser.getUsername());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You cannot change your own status");
            }

            // Get target user
            User user = userService.getUserById(id);
            if (user == null) {
                logger.error("User not found with id: {}", id);
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, 
                    "User not found with id " + id);
            }

            // Update status
            user.setEnabled(enabled);
            User updatedUser = userService.updateUserStatus(id, enabled);

            // If user is disabled, revoke all their tokens
            if (!enabled) {
                refreshTokenService.revokeAllUserTokens(user);
                logger.info("All tokens revoked for user ID: {}", id);
            }

            UserResponse response = userMapper.toUserResponse(updatedUser);
            logger.info("Successfully updated status for user ID: {}", id);
            return ResponseEntity.ok(response);
            
        } catch (ResponseStatusException e) {
            logger.error("Error updating status for user ID {}: {}", id, e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error updating status for user ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, 
                "Error updating user status: " + e.getMessage()
            );
        }
    }

    @PostMapping("/{id}/revoke-tokens")
    public ResponseEntity<?> revokeUserTokens(@PathVariable Long id) {
        try {
            logger.info("Revoking all tokens for user ID: {}", id);
            
            // Проверяем, не пытается ли пользователь отозвать свои собственные токены
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            if (currentUser.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "You cannot revoke your own tokens");
            }

            User user = userService.getUserById(id);
            refreshTokenService.revokeAllUserTokens(user);
            
            logger.info("Successfully revoked all tokens for user ID: {}", id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Error revoking tokens for user ID {}: {}", id, e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                "Error revoking user tokens: " + e.getMessage());
        }
    }
}