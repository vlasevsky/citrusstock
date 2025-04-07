package com.citrusmall.citrusstock.service;

import com.citrusmall.citrusstock.configuration.JwtConfig;
import com.citrusmall.citrusstock.exception.TokenRefreshException;
import com.citrusmall.citrusstock.model.RefreshToken;
import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtConfig jwtConfig;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, JwtConfig jwtConfig) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtConfig = jwtConfig;
    }

    public Optional<RefreshToken> findByToken(String token) {
        logger.debug("Looking up refresh token in database");
        Optional<RefreshToken> refreshToken = refreshTokenRepository.findByToken(token);
        if (refreshToken.isPresent()) {
            logger.debug("Refresh token found for user: {}", refreshToken.get().getUser().getUsername());
        } else {
            logger.warn("Refresh token not found in database");
        }
        return refreshToken;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        logger.debug("Creating new refresh token for user: {}", user.getUsername());
        
        // Сначала отзываем все существующие токены пользователя
        revokeAllUserTokens(user);
        
        RefreshToken refreshToken = new RefreshToken(
            user,
            UUID.randomUUID().toString(),
            Instant.now().plusMillis(jwtConfig.getRefreshTokenExpiration())
        );
        
        RefreshToken savedToken = refreshTokenRepository.save(refreshToken);
        logger.debug("New refresh token created, expires at: {}", savedToken.getExpiryDate());
        return savedToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        logger.debug("Verifying refresh token expiration for user: {}", token.getUser().getUsername());
        
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            logger.warn("Refresh token expired for user: {}, token deleted", token.getUser().getUsername());
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        
        if (token.isRevoked()) {
            logger.warn("Refresh token was revoked for user: {}", token.getUser().getUsername());
            throw new TokenRefreshException(token.getToken(), "Refresh token was revoked. Please make a new signin request");
        }
        
        logger.debug("Refresh token is valid, expires at: {}", token.getExpiryDate());
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        logger.debug("Deleting all refresh tokens for user ID: {}", userId);
        refreshTokenRepository.deleteByUser_Id(userId);
    }

    @Transactional
    public void revokeAllUserTokens(User user) {
        logger.debug("Revoking all refresh tokens for user: {}", user.getUsername());
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    @Transactional
    public void deleteExpiredTokens() {
        logger.debug("Deleting all expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
    }
} 