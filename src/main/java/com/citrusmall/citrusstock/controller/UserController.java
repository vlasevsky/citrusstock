package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.UserCreateRequest;
import com.citrusmall.citrusstock.dto.UserResponse;
import com.citrusmall.citrusstock.mapper.UserMapper;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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
}