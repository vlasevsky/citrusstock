package com.citrusmall.citrusstock.controller;

import com.citrusmall.citrusstock.dto.AuthRequest;
import com.citrusmall.citrusstock.dto.AuthResponse;
import com.citrusmall.citrusstock.dto.RefreshTokenRequest;
import com.citrusmall.citrusstock.exception.TokenRefreshException;
import com.citrusmall.citrusstock.model.RefreshToken;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.security.JwtTokenUtil;
import com.citrusmall.citrusstock.service.RefreshTokenService;
import com.citrusmall.citrusstock.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, 
                         JwtTokenUtil jwtTokenUtil, 
                         UserService userService,
                         RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        logger.info("Login attempt for user: {}", request.getUsername());
        try {
            // Проверяем существование пользователя перед аутентификацией
            User userCheck = userService.getUserByUsername(request.getUsername());
            if (userCheck == null) {
                logger.error("User not found: {}", request.getUsername());
                throw new AuthenticationException("User not found") {};
            }
            logger.debug("User found in database: {}", userCheck.getUsername());

            // Пытаемся аутентифицировать
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            logger.debug("User {} successfully authenticated", request.getUsername());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            User user = userService.getUserByUsername(request.getUsername());
            logger.debug("User details retrieved for {}", user.getUsername());

            String accessToken = jwtTokenUtil.generateAccessToken(user);
            logger.debug("Access token generated for user {}", user.getUsername());
            
            RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
            logger.debug("Refresh token generated for user {}", user.getUsername());

            logger.info("Login successful for user: {}", user.getUsername());
            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
        } catch (AuthenticationException e) {
            logger.error("Authentication failed for user {}: {}", request.getUsername(), e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error during login for user {}", request.getUsername(), e);
            throw e;
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        logger.info("Token refresh request received");
        logger.debug("Processing refresh token: {}", requestRefreshToken.substring(0, Math.min(10, requestRefreshToken.length())) + "...");

        try {
            return refreshTokenService.findByToken(requestRefreshToken)
                    .map(token -> {
                        logger.debug("Refresh token found in database");
                        RefreshToken verifiedToken = refreshTokenService.verifyExpiration(token);
                        logger.debug("Refresh token verified");
                        
                        User user = verifiedToken.getUser();
                        logger.debug("Generating new tokens for user: {}", user.getUsername());
                        
                        String accessToken = jwtTokenUtil.generateAccessToken(user);
                        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                        
                        logger.info("Token refresh successful for user: {}", user.getUsername());
                        return ResponseEntity.ok(new AuthResponse(accessToken, newRefreshToken.getToken()));
                    })
                    .orElseThrow(() -> {
                        logger.error("Refresh token not found in database: {}", requestRefreshToken.substring(0, Math.min(10, requestRefreshToken.length())) + "...");
                        return new TokenRefreshException(requestRefreshToken, "Refresh token is not in database!");
                    });
        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            logger.info("Logout request for user: {}", user.getUsername());
            
            refreshTokenService.revokeAllUserTokens(user);
            logger.debug("All refresh tokens revoked for user: {}", user.getUsername());
            
            logger.info("Logout successful for user: {}", user.getUsername());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage(), e);
            throw e;
        }
    }
} 