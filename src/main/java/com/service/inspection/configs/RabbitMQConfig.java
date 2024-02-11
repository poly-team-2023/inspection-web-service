package com.service.inspection.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class RabbitMQConfig {

    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setPassword("A5Tr02}\"B{w~");
        return connectionFactory;
    }

    @Bean
    public RabbitTemplate template() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory());
        return rabbitTemplate;
    }

    @Bean
    public AsyncRabbitTemplate asyncRabbitTemplate(RabbitTemplate template) {
        AsyncRabbitTemplate asyncRabbitTemplate = new AsyncRabbitTemplate(template);
        asyncRabbitTemplate.setReceiveTimeout(6000000);
        return asyncRabbitTemplate;
    }


    @Bean
    public RabbitAdmin rabbitAdmin(CachingConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean(value = "imageTask")
    public Queue queue() {
        return new Queue("img.task", true);
    }

    @Bean(name = "inspectionTask")
    public Queue documentTaskPublishQ() {
        return new Queue("doc.task", true);
    }

    @Bean
    public Queue gptTaskPublisher() {
        return new Queue("nlm.task", true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
        simpleMessageConverter.addAllowedListPatterns("com.service.inspection.service.*");
        return simpleMessageConverter;
    }
}
