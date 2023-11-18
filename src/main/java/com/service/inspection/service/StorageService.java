package com.service.inspection.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.service.inspection.configs.BucketName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
@AllArgsConstructor
public class StorageService {

    private final AmazonS3 amazonS3;

    // TODO правильная обработка ошибок

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveFile(BucketName bucketName, String key, MultipartFile multipartFile) {
        InputStream inputStream;
        try {
            inputStream = multipartFile.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException();
        }

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        if (!amazonS3.doesBucketExist(bucketName.getBucket())) {
            amazonS3.createBucket(bucketName.getBucket());
        }
        amazonS3.putObject(bucketName.getBucket(), key, inputStream, objectMetadata);
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

    // TODO: deleteFile()


    @Data
    @AllArgsConstructor
    public static class BytesWithContentType {
        private byte[] bytes;
        private String contentType;
    }
}
