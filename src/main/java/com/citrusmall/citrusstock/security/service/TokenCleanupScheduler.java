package com.citrusmall.citrusstock.security.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupScheduler {

    private final RefreshTokenService refreshTokenService;

    @Scheduled(cron = "0 0 * * * ?") // Каждый час
    public void cleanupExpiredTokens() {
        log.info("Запуск планового задания очистки устаревших токенов");
        refreshTokenService.cleanupExpiredTokens();
    }
} 