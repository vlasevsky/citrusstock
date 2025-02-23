package com.citrusmall.citrusstock.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Конфигурируем CORS для всех эндпоинтов.
     * Разрешаем запросы из http://localhost:3000, а также все HTTP-методы и заголовки.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // для всех URL
                .allowedOrigins("http://localhost:3000") // разрешаем запросы с указанного домена
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // разрешаем указанные HTTP-методы
                .allowedHeaders("*") // разрешаем любые заголовки
                .allowCredentials(true); // разрешаем отправку куки/учётных данных
    }
}
