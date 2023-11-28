package com.service.inspection.configs;

import java.text.MessageFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmailConfig {

    @Bean
    public MessageFormat defaultMessage(){
        return new MessageFormat("""
                Новая заявка от: {0}\n
                Заявка от лица компании: {1}
                Почта для связи: {2}\n
                Номер для связи: {3}\n
                Детали заявка: {4}\n
                """);
    }
}
