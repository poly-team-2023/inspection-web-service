package com.service.inspection.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.MessageFormat;

@Configuration
public class EmailConfig {

    @Bean
    public MessageFormat defaultMessage() {
        return new MessageFormat("""
                Новая заявка от: {0}
                
                Заявка от лица компании: {1}
                Почта для связи: {2}
                
                Номер для связи: {3}
                
                Детали заявка: {4}
                
                """);
    }
}
