package com.citrusmall.citrusstock.security;

import com.citrusmall.citrusstock.configuration.JwtConfig;
import com.citrusmall.citrusstock.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    
    private final JwtConfig jwtConfig;
    private final Key key;

    public JwtTokenUtil(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
        this.key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());
        logger.debug("JwtTokenUtil initialized with config: accessTokenExpiration={}, refreshTokenExpiration={}", 
            jwtConfig.getAccessTokenExpiration(), 
            jwtConfig.getRefreshTokenExpiration());
    }

    public String generateAccessToken(User user) {
        logger.debug("Generating access token for user: {}", user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getRole());
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", user.getUsername());
        claims.put("UserId", user.getId());
        String token = createToken(claims, user.getUsername(), jwtConfig.getAccessTokenExpiration());
        logger.debug("Access token generated for user: {}, expires in {} ms", user.getUsername(), jwtConfig.getAccessTokenExpiration());
        return token;
    }

    public String generateRefreshToken(User user) {
        logger.debug("Generating refresh token for user: {}", user.getUsername());
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ROLE_" + user.getRole());
        claims.put("http://schemas.xmlsoap.org/ws/2005/05/identity/claims/name", user.getUsername());
        claims.put("UserId", user.getId());
        String token = createToken(claims, user.getUsername(), jwtConfig.getRefreshTokenExpiration());
        logger.debug("Refresh token generated for user: {}, expires in {} ms", user.getUsername(), jwtConfig.getRefreshTokenExpiration());
        return token;
    }

    private String createToken(Map<String, Object> claims, String subject, Long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        logger.debug("Creating token for subject: {}, expires at: {}", subject, expiryDate);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(jwtConfig.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        logger.debug("Validating token for user: {}", userDetails.getUsername());
        try {
            final String username = getUsernameFromToken(token);
            boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
            logger.debug("Token validation result for user {}: {}", userDetails.getUsername(), isValid);
            return isValid;
        } catch (Exception e) {
            logger.error("Token validation failed for user {}: {}", userDetails.getUsername(), e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        logger.debug("Extracting username from token");
        String username = getClaimFromToken(token, Claims::getSubject);
        logger.debug("Username extracted from token: {}", username);
        return username;
    }

    public Date getExpirationDateFromToken(String token) {
        logger.debug("Extracting expiration date from token");
        Date expiration = getClaimFromToken(token, Claims::getExpiration);
        logger.debug("Token expiration date: {}", expiration);
        return expiration;
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        logger.debug("Extracting claim from token");
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {
        logger.debug("Parsing token claims");
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            logger.debug("Token claims parsed successfully");
            return claims;
        } catch (Exception e) {
            logger.error("Failed to parse token claims: {}", e.getMessage());
            throw e;
        }
    }

    private Boolean isTokenExpired(String token) {
        logger.debug("Checking token expiration");
        try {
            final Date expiration = getExpirationDateFromToken(token);
            boolean isExpired = expiration.before(new Date());
            logger.debug("Token expired: {}, expiration date: {}", isExpired, expiration);
            return isExpired;
        } catch (Exception e) {
            logger.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }
} 