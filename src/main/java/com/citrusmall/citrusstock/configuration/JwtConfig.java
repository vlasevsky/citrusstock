package com.citrusmall.citrusstock.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Validated
@Configuration
@ConfigurationProperties(prefix = "jwt", ignoreUnknownFields = false)
public class JwtConfig {
    @NotBlank(message = "JWT secret key must not be blank")
    private String secret = "9a4f2c8d3b7a1e6f45c8a0b3f267d8b1d4e6f3c8a9d2b5f8e1a7c4d9f2e5b8a";

    @NotNull(message = "Access token expiration must not be null")
    @Positive(message = "Access token expiration must be positive")
    private Long accessTokenExpiration = 10000L; // 10 seconds

    @NotNull(message = "Refresh token expiration must not be null")
    @Positive(message = "Refresh token expiration must be positive")
    private Long refreshTokenExpiration = 30000L; // 30 seconds

    @NotBlank(message = "JWT issuer must not be blank")
    private String issuer = "citrusstock";
} 