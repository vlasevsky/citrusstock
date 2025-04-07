package com.citrusmall.citrusstock.aop;

import com.citrusmall.citrusstock.model.User;
import com.citrusmall.citrusstock.service.UserService;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Aspect
@Component
public class UserActivityAspect {
    private static final Logger logger = LoggerFactory.getLogger(UserActivityAspect.class);
    
    private final UserService userService;

    public UserActivityAspect(UserService userService) {
        this.userService = userService;
    }

    @Before("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.DeleteMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PatchMapping)")
    public void updateUserLastActive() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
                if (authentication.getPrincipal() instanceof User) {
                    User user = (User) authentication.getPrincipal();
                    Instant now = Instant.now();
                    user.setLastActiveAt(now);
                    userService.updateLastActive(user.getId(), now);
                    logger.debug("Updated last active time for user: {} to {}", user.getUsername(), now);
                }
            }
        } catch (Exception e) {
            logger.error("Error updating user last active time: {}", e.getMessage(), e);
        }
    }
} 