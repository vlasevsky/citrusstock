package com.citrusmall.citrusstock.security.controller;

import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.security.dto.AuthRequest;
import com.citrusmall.citrusstock.security.dto.AuthResponse;
import com.citrusmall.citrusstock.security.dto.RefreshTokenRequest;
import com.citrusmall.citrusstock.security.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authorization", description = "API for user authorization")
public class AuthController {
    
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Authentication user", description = "Authentication by login and password")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Succesfull authentication",
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Incorrect credentions")
    })
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody AuthRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.login(request, httpRequest));
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "Обновление токена", description = "Обновление access токена с помощью refresh токена")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Токен успешно обновлен", 
                content = @Content(schema = @Schema(implementation = AuthResponse.class))),
        @ApiResponse(responseCode = "401", description = "Невалидный refresh токен")
    })
    public ResponseEntity<AuthResponse> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request,
            HttpServletRequest httpRequest) {
        return ResponseEntity.ok(authService.refreshToken(request, httpRequest));
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Выход из системы", description = "Завершение текущей сессии пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный выход из системы"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshTokenRequest request) {
        if (request != null && request.getRefreshToken() != null) {
            authService.logout(request.getRefreshToken());
        }
        return ResponseEntity.ok().build();
    }
    
    @PostMapping("/logout-all")
    @Operation(summary = "Выход из всех сессий", description = "Завершение всех сессий пользователя")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Успешный выход из всех сессий"),
        @ApiResponse(responseCode = "401", description = "Пользователь не аутентифицирован")
    })
    public ResponseEntity<Void> logoutAll(Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof User user) {
            authService.logoutAll(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(401).build();
    }
} 