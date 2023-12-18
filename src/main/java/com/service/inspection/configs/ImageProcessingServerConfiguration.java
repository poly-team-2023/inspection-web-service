package com.service.inspection.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@ConfigurationProperties(prefix = "image-processing.server")
@Configuration
@Getter
@Setter
public class ImageProcessingServerConfiguration {
    private String host;
    private String port;
    private Integer timeout;

    @Bean
    public WebClient getImageProcessingClient() {
        HttpClient client = HttpClient.create()
                .responseTimeout(Duration.ofMillis(timeout));
        return WebClient.builder()
                .baseUrl(createBaseUrl())
                .clientConnector(new ReactorClientHttpConnector(client))
                .build();
    }

    private String createBaseUrl() {
        return String.format("http://%s:%s/", host, port);
    }
}
