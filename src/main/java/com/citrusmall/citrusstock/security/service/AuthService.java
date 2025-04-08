package com.citrusmall.citrusstock.security.service;

import com.citrusmall.citrusstock.model.RefreshToken;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.security.dto.AuthRequest;
import com.citrusmall.citrusstock.security.dto.AuthResponse;
import com.citrusmall.citrusstock.security.dto.RefreshTokenRequest;
import com.citrusmall.citrusstock.security.exception.InvalidTokenException;
import com.citrusmall.citrusstock.security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    
    @Transactional
    public AuthResponse login(AuthRequest request, HttpServletRequest httpRequest) {
        // Аутентификация пользователя
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        
        // Получение пользователя и генерация токенов
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        User user = (User) userDetails;
        
        String accessToken = jwtService.generateToken(userDetails);
        
        // Создание и сохранение нового refresh токена
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, httpRequest);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }
    
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request, HttpServletRequest httpRequest) {
        final String refreshTokenStr = request.getRefreshToken();
        
        // Проверяем, что токен существует и валиден
        RefreshToken refreshToken = refreshTokenService.verifyExpiration(refreshTokenStr);
        User user = refreshToken.getUser();
        
        // Отзываем текущий refresh токен
        refreshTokenService.revokeToken(refreshTokenStr);
        
        // Генерация нового access token
        String accessToken = jwtService.generateToken(user);
        
        // Создание нового refresh токена
        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user, httpRequest);
        
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .build();
    }
    
    @Transactional
    public void logout(String token) {
        if (token != null && !token.isEmpty()) {
            refreshTokenService.findByToken(token)
                .ifPresent(refreshToken -> refreshTokenService.revokeToken(token));
        }
    }
    
    @Transactional
    public void logoutAll(User user) {
        refreshTokenService.revokeAllUserTokens(user);
    }
} 