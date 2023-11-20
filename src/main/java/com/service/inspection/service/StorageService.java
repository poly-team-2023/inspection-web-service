package com.service.inspection.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.service.inspection.configs.BucketName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class StorageService {

    private final AmazonS3 amazonS3;

    // TODO правильная обработка ошибок

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveFile(BucketName bucketName, String key, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentType(multipartFile.getContentType());
            objectMetadata.setContentLength(multipartFile.getSize());

            if (!amazonS3.doesBucketExist(bucketName.getBucket())) {
                amazonS3.createBucket(bucketName.getBucket());
            }
            amazonS3.putObject(bucketName.getBucket(), key, inputStream, objectMetadata);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public BytesWithContentType getFile(BucketName bucketName, String key) {
        S3Object s3Object = amazonS3.getObject(bucketName.getBucket(), key);
        byte[] fileBytes;
        try {
            fileBytes = s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return new BytesWithContentType(fileBytes, s3Object.getObjectMetadata().getContentType());
    }

    @Async("fileAsyncExecutor")
    public CompletableFuture<BytesWithContentType> getFileAsync(BucketName bucketName, String key) {
        S3Object s3Object = amazonS3.getObject(bucketName.getBucket(), key);
        byte[] fileBytes;
        try {
            fileBytes = s3Object.getObjectContent().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return CompletableFuture
                .completedFuture(new BytesWithContentType(fileBytes, s3Object.getObjectMetadata().getContentType()));
    }

    @Data
    @AllArgsConstructor
    public static class BytesWithContentType {
        private byte[] bytes;
        private String contentType;
    }
}
