package com.service.inspection.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.AsyncRabbitTemplate;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.security.KeyStore;

@Configuration
@ConfigurationProperties(prefix = "rabbit")
@Getter
@Setter
public class RabbitMQConfig {


        private String host;
        private String password;
        private Integer port;

        @Value("${rabbit.queue.image}")
        private String queueImage;

        @Value("${rabbit.queue.nlm}")
        private String queueNlm;

        @Value("${rabbit.queue.main}")
        private String queueMain;

        @Value("${rabbit.passphrase}")
        private String passphrase;

        @Bean
        public com.rabbitmq.client.ConnectionFactory createMega() throws Exception {
            char[] keyPassphrase = passphrase.toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(new ClassPathResource("cert\\client.keystore").getInputStream(), keyPassphrase);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, keyPassphrase);

            char[] trustPassphrase = passphrase.toCharArray();
            KeyStore tks = KeyStore.getInstance("JKS");
            tks.load(new ClassPathResource("cert\\client.truststore").getInputStream(), trustPassphrase);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(tks);

            SSLContext c = SSLContext.getInstance("TLSv1.3");
            c.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

            com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
            factory.setHost(host);
            factory.setPort(port);
            factory.useSslProtocol(c);
            factory.setPassword(password);
            factory.enableHostnameVerification();

            return factory;
        }

    @Bean
    public ConnectionFactory rabbitConnectionFactory(com.rabbitmq.client.ConnectionFactory cf) {
        return new CachingConnectionFactory(cf);
    }

    @Bean
    public RabbitTemplate template(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
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
        return new Queue(queueImage, true);
    }

    @Bean(name = "inspectionTask")
    public Queue documentTaskPublishQ() {
        return new Queue(queueMain, true);
    }

    @Bean(name = "nlmTask")
    public Queue gptTaskPublisher() {
        return new Queue(queueNlm, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        SimpleMessageConverter simpleMessageConverter = new SimpleMessageConverter();
        simpleMessageConverter.addAllowedListPatterns("com.service.inspection.service.*");
        return simpleMessageConverter;
    }
}
