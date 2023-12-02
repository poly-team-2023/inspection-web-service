package com.service.inspection.configs;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

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
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(accessKey, secretKey);
    }

    @Bean
    public ClientConfiguration clientConfiguration() {
        ClientConfiguration configuration = new ClientConfiguration();
        configuration.setSocketTimeout(timeout);
        configuration.setSignerOverride("AWSS3V4SignerType");
        return configuration;
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(host, region))
                .withClientConfiguration(clientConfiguration())
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
