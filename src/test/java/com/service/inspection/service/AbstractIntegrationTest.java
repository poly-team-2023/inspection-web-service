package com.service.inspection.service;

import com.service.inspection.configs.RabbitMQConfig;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration")
public abstract class AbstractIntegrationTest extends AbstractTestContainerStartUp {
    @MockBean
    private RabbitAdmin rabbitAdmin;

    @MockBean
    private RabbitMQConfig rabbitMQConfig;

    @MockBean
    private AsyncRabbitTemplate rabbitAsyncTemplate;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean(name="imageTask")
    private Queue queue;

    @MockBean(name="inspectionTask")
    private Queue queue1;

    @MockBean(name="nlmTask")
    private Queue queue2;
}
