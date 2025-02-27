package com.citrusmall.citrusstock.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Around("execution(* com.citrusmall.citrusstock.service..*(..)) || execution(* com.citrusmall.citrusstock.controller..*(..))")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("Entering {} with arguments = {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            logger.info("Exiting {} with result = {}", joinPoint.getSignature().toShortString(), result);
            return result;
        } catch (IllegalArgumentException e) {
            logger.error("Illegal argument {} in {}",
                    joinPoint.getArgs(), joinPoint.getSignature().toShortString());
            throw e;
        }
    }


    @Before("execution(* com.citrusmall.citrusstock.controller..*(..))")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Before {}: {}", joinPoint.getSignature().toShortString(), joinPoint.getArgs());
    }

    @AfterReturning(pointcut = "execution(* com.citrusmall.citrusstock.controller..*(..))", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("After {} returned with value = {}", joinPoint.getSignature().toShortString(), result);
    }
}
