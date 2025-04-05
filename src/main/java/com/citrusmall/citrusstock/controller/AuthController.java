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
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.getUserByUsername(request.getUsername());

        String accessToken = jwtTokenUtil.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken.getToken()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtTokenUtil.generateAccessToken(user);
                    RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(user);
                    return ResponseEntity.ok(new AuthResponse(accessToken, newRefreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken,
                        "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        refreshTokenService.revokeAllUserTokens(user);
        return ResponseEntity.ok().build();
    }
} 