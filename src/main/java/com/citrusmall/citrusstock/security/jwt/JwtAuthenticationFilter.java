package com.citrusmall.citrusstock.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // Если заголовок авторизации отсутствует или не начинается с "Bearer ", то пропускаем запрос дальше
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Извлекаем JWT из заголовка
        jwt = authHeader.substring(7);
        
        try {
            // Извлекаем имя пользователя из JWT
            username = jwtService.extractUsername(jwt);
            
            // Если имя пользователя есть и пользователь еще не аутентифицирован
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Загружаем пользователя из базы данных
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // Проверяем валидность токена
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    // Создаем токен аутентификации
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Устанавливаем детали запроса
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    
                    // Устанавливаем аутентификацию в контекст безопасности
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            // Если во время обработки токена возникла ошибка, игнорируем и продолжаем цепочку фильтров
            logger.error("JWT token processing error", e);
        }
        
        // Продолжаем цепочку фильтров
        filterChain.doFilter(request, response);
    }
} 