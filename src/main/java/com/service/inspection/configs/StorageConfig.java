package com.service.inspection.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration
@ConfigurationProperties(prefix = "aws.s3")
@Getter
@Setter
public class StorageConfig {

    private String accessKey;
    private String secretKey;
    private String host;
    private int timeout;
    private String region;

    @Bean
    public StaticCredentialsProvider awsCredentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey));
    }

    @Bean
    public S3Client amazonS3(StaticCredentialsProvider staticCredentialsProvider) {
        return S3Client.builder().credentialsProvider(staticCredentialsProvider)
                .region(Region.of(region))
                .endpointOverride(URI.create(host))
                .forcePathStyle(true)
                .build();
    }

}
