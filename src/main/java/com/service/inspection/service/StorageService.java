package com.service.inspection.service;

import com.service.inspection.configs.BucketName;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

@Service
@AllArgsConstructor
public class StorageService {

    private final S3Client amazonS3;

    // TODO правильная обработка ошибок

    @Transactional(propagation = Propagation.MANDATORY)
    public void saveFile(BucketName bucketName, String key, MultipartFile multipartFile) {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            saveFile(bucketName, key, inputStream);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public void saveFile(BucketName bucketName, String key, InputStream inputStream) {
        try {
            if (amazonS3.listBuckets().buckets().stream().map(Bucket::name)
                    .noneMatch(b -> Objects.equals(b, bucketName.getBucket()))) {
                amazonS3.createBucket(b -> b.bucket(bucketName.getBucket()).build());
            }

            amazonS3.putObject(
                    b -> b.bucket(bucketName.getBucket()).key(key),
                    RequestBody.fromInputStream(inputStream, inputStream.available())
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    public BytesWithContentType getFile(BucketName bucketName, String key) {
        ResponseInputStream<GetObjectResponse> responseInputStream =
                amazonS3.getObject(b -> b.bucket(bucketName.getBucket()).key(key).build());
        byte[] fileBytes;
        try {
            fileBytes = responseInputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return new BytesWithContentType(fileBytes, responseInputStream.response().contentType());
    }

    @Data
    @AllArgsConstructor
    public static class BytesWithContentType {
        private byte[] bytes;
        private String contentType;
        private String name;

        public BytesWithContentType(byte[] bytes, String contentType) {
            this.bytes = bytes;
            this.contentType = contentType;
        }
    }
}
